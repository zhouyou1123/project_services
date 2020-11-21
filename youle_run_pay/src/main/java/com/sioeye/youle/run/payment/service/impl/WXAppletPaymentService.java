package com.sioeye.youle.run.payment.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumOrdersStatus;
import com.sioeye.youle.run.payment.config.EnumPayWay;
import com.sioeye.youle.run.payment.config.EnumTradeType;
import com.sioeye.youle.run.payment.config.RabbitMqConfig;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.service.intf.IWXAppletPayment;
import com.sioeye.youle.run.payment.util.ConstUtil;
import com.sioeye.youle.run.payment.util.HttpUtil;
import com.sioeye.youle.run.payment.util.Util;
import com.sioeye.youle.run.payment.util.ValidateUtil;
import com.sioeye.youle.run.payment.wxpay.PayCommonUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName WXAppletPaymentService.java
 *
 * @todo 微信小程序游乐园支付实现类
 */
@Service
@RefreshScope
public class WXAppletPaymentService implements IWXAppletPayment {

	@Value("${sioeye.weixin.appletface.id}")
	private String sioeyeWeixinAppletId;// 小程序id(小程序和公众号用的是同一个wx账号)
	@Value("${sioeye.weixin.appletface.key}")
	private String sioeyeWeixinAppletKey;// 小程序key
	@Value("${sioeye.weixin.appletface.mch.id}")
	private String sioeyeWeixinAppletMchId;// 小程序mchid
	@Value("${weixin.notify.url}")
	private String weixinNotifyUrl;// sioeye微信回调地址
	@Value("${weixin.unified.order.url}")
	private String weixinUnifiedOrderUrl;// 微信支付下单地址
	@Value("${weixin.order.query.url}")
	private String weixinOrderQueryUrl;// 支付详情查询地址
	@Value("${sioeye.key}")
	private String sioeyeKey;// sioeye key
	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Autowired
	private IPaymentDao iPaymentDao;
	@Autowired
	private RabbitMqConfig rabbitMqConfig;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		this.validateMap(map);
		// 小程序预订单处理
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("appid", sioeyeWeixinAppletId);
		parameters.put("mch_id", sioeyeWeixinAppletMchId);
		// 创建16位随机数
		String nonceStr = Util.createNoncestr(16);
		parameters.put("nonce_str", nonceStr);
		// 商品介绍，支付时显示的提示
		parameters.put("body", map.get("paymentDescription"));
		// 订单号
		parameters.put("out_trade_no", map.get("orderId"));
		// 附加数据，系统自定义的加密信息
		String sioeyeSign = PayCommonUtil.getSioeyeSign(System.currentTimeMillis(), map.get("orderId").toString(),
				sioeyeWeixinAppletId, nonceStr, sioeyeKey);
		parameters.put("attach", sioeyeSign);
		// 小程序用户的openid
		parameters.put("openid", map.get("openId"));
		BigDecimal promoteTotal = new BigDecimal(map.get("actualAmount").toString()).multiply(new BigDecimal(100));
		parameters.put("total_fee", promoteTotal.intValue() + "");
		// 如果总费用为0(使用优惠劵),那么不需要调用微信，直接创建一个免费订单,并将prepay_id设置为disney_free_payment
		if (this.validatePrice(sioeyeSign, nonceStr, map, result)) {
			return result;
		}
		parameters.put("spbill_create_ip", map.get("spbillCreateIp"));
		parameters.put("notify_url", weixinNotifyUrl);
		parameters.put("trade_type", "JSAPI");
		// 创建微信签名sign
		parameters.put("sign", PayCommonUtil.createSign("UTF-8", parameters, sioeyeWeixinAppletKey));
		// 调用微信统一下单接口
		String resultStr = HttpUtil.httpsRequest(weixinUnifiedOrderUrl, "POST", PayCommonUtil.getRequestXml(parameters),
				"application/x-www-form-urlencoded");
		// 解析微信返回的信息，以Map形式存储便于取值
		SortedMap<String, String> returnMap = PayCommonUtil.doXMLParse(resultStr);
		// 判断返回值
		if ("FAIL".equals(returnMap.get("return_code"))) {
			EnumHandle.WEIXIN_UNIFIED_ORDER_FAILED.setMessage(returnMap.get("return_msg"));
			throw new CustomException(EnumHandle.WEIXIN_UNIFIED_ORDER_FAILED);
		}
		if ("FAIL".equals(returnMap.get("result_code"))) {
			EnumHandle.WEIXIN_UNIFIED_ORDER_FAILED.setMessage(returnMap.get("err_code_des"));
			throw new CustomException(EnumHandle.WEIXIN_UNIFIED_ORDER_FAILED);
		}
		if (!PayCommonUtil.validityWeiXinReturnMap(returnMap)) {
			throw new CustomException(EnumHandle.WEIXIN_RETURN_PARAMS_ERROR);
		}
		// 二次签名
		if ("SUCCESS".equals(returnMap.get("return_code")) && "SUCCESS".equals(returnMap.get("result_code"))) {
			// 调用插入payment方法
			Payment payment = new Payment();
			this.insertPayment(sioeyeSign, map, returnMap, result, payment);
			return result;
		}
		throw new CustomException(EnumHandle.WEIXIN_RETURN_PARAMS_ERROR);
	}

	/**
	 * 验证map参数
	 * 
	 * @param map
	 *            void
	 */
	private void validateMap(Map<String, Object> map) {
		ValidateUtil.validateEmptyAndString(map.get("paymentDescription"), EnumHandle.PARAMS_ERROR);
		ValidateUtil.validateEmptyAndString(map.get("orderId"), EnumHandle.PARAMS_ERROR);
		if (map.get("orderId").toString().length() != 32) {
			throw new CustomException(EnumHandle.PARAMS_ERROR);
		}
		ValidateUtil.validateEmptyAndString(map.get("openId"), EnumHandle.PARAMS_ERROR);
		ValidateUtil.validateEmptyAndPrice(map.get("actualAmount"), EnumHandle.PARAMS_ERROR);
		ValidateUtil.validateEmptyAndString(map.get("spbillCreateIp"), EnumHandle.PARAMS_ERROR);
	}

	/**
	 * 判断价格是否是免费的支付
	 * 
	 * @param sioeyeSign
	 * @param nonceStr
	 * @param map
	 * @param result
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	private boolean validatePrice(String sioeyeSign, String nonceStr, Map<String, Object> map, JSONObject result)
			throws CustomException, Exception {
		if (Double.parseDouble(map.get("actualAmount").toString()) == 0) {
			SortedMap<String, String> returnMap = new TreeMap<>();
			returnMap.put("prepay_id", ConstUtil.DISNEY_FREE_PAYMENT.toString());
			returnMap.put("nonce_str", nonceStr);
			Payment payment = new Payment();
			payment.setPayTime(new Date());
			// 调用插入payment方法
			insertPayment(sioeyeSign, map, returnMap, result, payment);
			return true;
		}
		return false;
	}

	/**
	 * 插入支付信息
	 * 
	 * @param sioeyeSign
	 * @param openId
	 * @param map
	 * @param result
	 * @param payment
	 * @throws Exception
	 * @throws CustomException
	 */
	private void insertPayment(String sioeyeSign,Map<String, Object> map, SortedMap<String, String> returnMap, JSONObject result,
			Payment payment) throws CustomException, Exception {
		SortedMap<Object, Object> secondParameters = new TreeMap<Object, Object>();
		secondParameters.put("appId", sioeyeWeixinAppletId);
		secondParameters.put("nonceStr", returnMap.get("nonce_str"));
		secondParameters.put("package=prepay_id", returnMap.get("prepay_id"));
		long currentTime = System.currentTimeMillis();
		secondParameters.put("signType", "MD5");
		secondParameters.put("timeStamp", currentTime / 1000);
		String secondSign = PayCommonUtil.createSign("UTF-8", secondParameters, sioeyeWeixinAppletKey);
		// 1：微信支付
		payment.setPayWay(EnumPayWay.WEIXIN.getCode());
		payment.setSign(secondSign);
		payment.setNonceStr(returnMap.get("nonce_str"));
		if (map.get("tradeType") == null) {     
			payment.setTradeType(EnumTradeType.APPLET_DISNEY.toString());
		} else {
			payment.setTradeType(map.get("tradeType").toString());
		}
		payment.setPrepayId(returnMap.get("prepay_id"));
		payment.setSioeyeSign(sioeyeSign);
		payment.setOpenId(map.get("openId").toString());
		payment.setUpdateTime(new Date(currentTime));
		if (!(iPaymentDao.save(payment) > 0)) {
			throw new CustomException(EnumHandle.INSERT_PAYMENT_FAILED);
		}
		// 封装返回值
		this.packageResult(result, payment);
	}

	/**
	 * 封装返回值
	 * 
	 * @param result
	 * @param payment
	 */
	private void packageResult(JSONObject result, Payment payment) {
		// 设置返回值
		result.put("objectId", payment.getObjectId());
		result.put("payWay", payment.getPayWay());
		result.put("sign", payment.getSign());
		result.put("tradeType", payment.getTradeType());
		result.put("prepayId", payment.getPrepayId());
		result.put("openId", payment.getOpenId());
		result.put("payTime", payment.getPayTime());
		result.put("updateTime", payment.getUpdateTime());
		result.put("isSandboxFlag", payment.getIosSandboxFlag());
		result.put("appId", sioeyeWeixinAppletId);
		result.put("nonceStr", payment.getNonceStr());
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		// 小程序支付查询
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		parameters.put("appid", sioeyeWeixinAppletId);
		parameters.put("mch_id", sioeyeWeixinAppletMchId);
		parameters.put("out_trade_no", map.get("orderId"));
		// 创建16位随机数
		String nonceStr = Util.createNoncestr(16);
		parameters.put("nonce_str", nonceStr);
		parameters.put("sign", PayCommonUtil.createSign("UTF-8", parameters, sioeyeWeixinAppletKey));
		// 调微信查询接口
		String resultStr = HttpUtil.httpsRequest(weixinOrderQueryUrl, "POST", PayCommonUtil.getRequestXml(parameters),
				"application/x-www-form-urlencoded");
		// 解析微信返回的信息，以Map形式存储便于取值
		Map<String, String> resultmap = PayCommonUtil.doXMLParse(resultStr);
		if ("FAIL".equals(resultmap.get("return_code"))) {
			throw new CustomException(EnumHandle.QUERY_ORDER_FAILED);
		}
		if ("SUCCESS".equals(resultmap.get("return_code")) && "SUCCESS".equals(resultmap.get("result_code"))) {
			// 查询微信的支付结果
			result.put("tradeState", resultmap.get("trade_state"));
			result.put("tradeStateDesc", resultmap.get("trade_state_desc"));
			result.put("appId", sioeyeWeixinAppletId);
			// 第三方支付已完成，并且当前订单还是未支付
			if ("SUCCESS".equals(resultmap.get("trade_state"))
					&& Integer.parseInt(map.get("orderStatus").toString()) != EnumOrdersStatus.PAY_SUCCESS.getCode()) {
				Map<String, String> mqMap = new HashMap<>();
				mqMap.put("orderId", map.get("orderId").toString());
				this.sendMqMessage(mqMap);
			}
		} else {
			result.put("errCode", resultmap.get("err_code"));
			result.put("errCodeDes", resultmap.get("err_code_des"));
			result.put("appId", sioeyeWeixinAppletId);
		}
		return result;
	}

	/**
	 * 发送mq消息
	 * 
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	private boolean sendMqMessage(Map<String, String> map) throws IOException, TimeoutException {
		// 创建连接
		rabbitMqConfig.createMqConnection();
		// 创建消息
		JSONObject message = new JSONObject();
		message.put("orderId", map.get("orderId"));
		message.put("openId", map.get("openId"));
		message.put("prepayId", map.get("prepayId"));
		// 支付成功，向指定的mq消息队列中发送支付完成的消息
		Util.sendMqMessage(rabbitMqConfig.mqConnection, orderPaymentQueue, message.toJSONString());
		return true;
	}

	@Override
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		if (!"SUCCESS".equalsIgnoreCase(map.get("result_code"))
				|| !"SUCCESS".equalsIgnoreCase(map.get("return_code"))) {
			throw new CustomException(EnumHandle.PAY_BACK_WEIXIN_FAILED);
		}
		// 判断价格是否正确
		String actualAmountStr = iPaymentDao.queryActualAmountByOrderId(map.get("out_trade_no").toString());
		ValidateUtil.validateEmptyAndString(actualAmountStr, EnumHandle.PAY_BACK_PARAM_INCORRECT);
		BigDecimal promoteTotal = new BigDecimal(actualAmountStr).multiply(new BigDecimal(100));
		if (map.get("total_fee") == null || "".equals(map.get("total_fee"))
				|| !map.get("total_fee").equals(promoteTotal.intValue() + "")) {
			throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
		}
		// 判断微信返回值签名是否正确
		if (map.get("sign") == null
				|| !PayCommonUtil.createSignStr("UTF-8", map, sioeyeWeixinAppletKey).equals(map.get("sign"))) {
			throw new CustomException(EnumHandle.PAY_BACK_SIGN_INCORRECT);
		}
		Payment payment = iPaymentDao.queryPaymentByOrderId(map.get("out_trade_no").toString());
		// 先判断返回信息是否正确
		ValidateUtil.validateEmptyAndString(map.get("attach"), EnumHandle.PAY_BACK_PARAM_INCORRECT);
		if (!map.get("attach").equals(payment.getSioeyeSign())) {
			throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
		}
		// 设置支付时间
		payment.setPayTime(new Date());
		// 处理payment支付成功
		if (!(iPaymentDao.update(payment) > 0)) {
			throw new CustomException(EnumHandle.UPDATE_PAYMENT_FAILED);
		}
		map.put("orderId", map.get("out_trade_no"));
		map.put("openId", payment.getOpenId());
		map.put("prepayId", payment.getPrepayId());
		// 回调小程序的时候需要的参数
		result.put("success", this.sendMqMessage(map));
		return result;
	}
}
