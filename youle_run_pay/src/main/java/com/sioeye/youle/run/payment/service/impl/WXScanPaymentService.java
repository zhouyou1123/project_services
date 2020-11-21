package com.sioeye.youle.run.payment.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumPayWay;
import com.sioeye.youle.run.payment.config.RabbitMqConfig;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.service.intf.IWXScanPayment;
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
 * @date 2019年8月8日
 *
 * @fileName WXScanPaymentService.java
 *
 * @todo 微信扫描支付实现类
 */
@Service
public class WXScanPaymentService implements IWXScanPayment {

	@Value("${sioeye.weixin.appletface.id}")
	private String weixinAppId;// 小程序id(小程序和公众号用的是同一个wx账号)
	@Value("${sioeye.weixin.appletface.key}")
	private String weixinAppKey;// 小程序key
	@Value("${sioeye.weixin.appletface.mch.id}")
	private String weixinAppMchId;// 小程序mchid
	@Value("${sioeye.key}")
	private String sioeyeKey;// sioeye key
	@Value("${weixin.notify.url}")
	private String weixinNotifyUrl;// sioeye微信回调地址
	@Value("${weixin.unified.order.url}")
	private String weixinUnifiedOrderUrl;// 微信支付下单地址
	@Value("${sioeye.weixin.order.expire}")
	private int sioeyeWeixinOrderExpire;
	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Autowired
	private IPaymentDao iPaymentDao;
	@Autowired
	private RabbitMqConfig rabbitMqConfig;

	private static final Log logger = LogFactory.getLog(WXScanPaymentService.class);

	@Override
	public JSONObject prePayment(Map<String, Object> params) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		// 小程序预订单处理
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		// 创建16位随机数
		String nonceStr = Util.createNoncestr(16);
		parameters.put("nonce_str", nonceStr);
		// 附加数据，系统自定义的加密信息
		String sioeyeSign = PayCommonUtil.getSioeyeSign(System.currentTimeMillis(), params.get("orderId").toString(),
				weixinAppId, nonceStr, sioeyeKey);
		parameters.put("attach", sioeyeSign);
		// 价格
		BigDecimal promoteTotal = new BigDecimal(params.get("actualAmount").toString()).multiply(new BigDecimal(100));
		parameters.put("total_fee", promoteTotal.intValue() + "");
		logger.info("pay===tradeType:" + params.get("tradeType").toString());
		logger.info("pay===total_fee:" + promoteTotal.intValue() + "");
		// 如果总费用为0(使用优惠劵),那么不需要调用微信，直接创建一个免费订单,并将prepay_id设置为disney_free_payment
		if (this.validatePrice(params.get("tradeType").toString(), parameters, result)) {
			return result;
		}
		// 商品介绍，支付时显示的提示
		parameters.put("body", params.get("paymentDescription"));
		// 订单号
		parameters.put("out_trade_no", params.get("orderId"));
		parameters.put("appid", weixinAppId);
		parameters.put("mch_id", weixinAppMchId);
		parameters.put("spbill_create_ip", params.get("spbillCreateIp"));
		parameters.put("notify_url", weixinNotifyUrl);
		parameters.put("trade_type", "NATIVE");
		// 取得指定时区的时间：
		parameters.put("time_expire", Util.createTimeExpire("Asia/Shanghai", sioeyeWeixinOrderExpire));
		// 创建微信签名sign
		parameters.put("sign", PayCommonUtil.createSign("UTF-8", parameters, weixinAppKey));
		// 调用微信统一下单接口
		String resultStr = HttpUtil.httpsRequest(weixinUnifiedOrderUrl, "POST", PayCommonUtil.getRequestXml(parameters),
				"application/x-www-form-urlencoded");
		// 解析微信返回的信息，以Map形式存储便于取值
		SortedMap<String, String> returnMap;
		logger.info("pay===weixin result data:" + resultStr);
		returnMap = PayCommonUtil.doXMLParse(resultStr);
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
			this.insertPayment(sioeyeSign, params.get("tradeType").toString(), returnMap, result, payment);
			return result;
		}
		logger.info("pay===weixin result value:" + result);
		return result;
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
	private boolean validatePrice(String tradeType, SortedMap<Object, Object> params, JSONObject result)
			throws CustomException, Exception {
		if (Double.parseDouble(params.get("total_fee").toString()) == 0) {
			SortedMap<String, String> returnMap = new TreeMap<>();
			returnMap.put("prepay_id", ConstUtil.DISNEY_FREE_PAYMENT.toString());
			returnMap.put("nonce_str", params.get("nonce_str").toString());
			returnMap.put("code_url", "");
			// 调用插入payment方法
			Payment payment = new Payment();
			payment.setPayTime(new Date());
			this.insertPayment(params.get("attach").toString(), tradeType, returnMap, result, payment);
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
	private void insertPayment(String sioeyeSign, String tradeType, SortedMap<String, String> returnMap,
			JSONObject result, Payment payment) throws CustomException, Exception {
		SortedMap<Object, Object> secondParameters = new TreeMap<Object, Object>();
		secondParameters.put("appId", weixinAppId);
		secondParameters.put("nonceStr", returnMap.get("nonce_str"));
		// secondParameters.put("package=prepay_id",
		// returnMap.get("prepay_id"));
		secondParameters.put("codeUrl", returnMap.get("code_url"));
		long currentTime = System.currentTimeMillis();
		secondParameters.put("signType", "MD5");
		secondParameters.put("timeStamp", currentTime / 1000);
		String secondSign = PayCommonUtil.createSign("UTF-8", secondParameters, weixinAppKey);
		// 1：微信支付
		payment.setPayWay(EnumPayWay.WEIXIN.getCode());
		payment.setSign(secondSign);
		payment.setNonceStr(returnMap.get("nonce_str"));
		payment.setTradeType(tradeType);
		payment.setPrepayId(returnMap.get("prepay_id"));
		payment.setSioeyeSign(sioeyeSign);
		payment.setUpdateTime(new Date(currentTime));
		if (!(iPaymentDao.save(payment) > 0)) {
			throw new CustomException(EnumHandle.INSERT_PAYMENT_FAILED);
		}
		// 封装返回值
		this.packageResult(result, payment, returnMap.get("code_url"));
	}

	/**
	 * 封装返回值
	 * 
	 * @param result
	 * @param payment
	 */
	private void packageResult(JSONObject result, Payment payment, String codeUrl) {
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
		result.put("appId", weixinAppId);
		result.put("nonceStr", payment.getNonceStr());
		result.put("codeUrl", codeUrl);
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject payBack(Map<String, String> params) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		if (!"SUCCESS".equalsIgnoreCase(params.get("result_code"))
				|| !"SUCCESS".equalsIgnoreCase(params.get("return_code"))) {
			throw new CustomException(EnumHandle.PAY_BACK_WEIXIN_FAILED);
		}
		// 判断价格是否正确
		String actualAmountStr = iPaymentDao.queryActualAmountByOrderId(params.get("out_trade_no").toString());
		ValidateUtil.validateEmptyAndString(actualAmountStr, EnumHandle.PAY_BACK_PARAM_INCORRECT);
		BigDecimal promoteTotal = new BigDecimal(actualAmountStr).multiply(new BigDecimal(100));
		if (params.get("total_fee") == null || "".equals(params.get("total_fee"))
				|| !params.get("total_fee").equals(promoteTotal.intValue() + "")) {
			throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
		}
		// 判断微信返回值签名是否正确
		if (params.get("sign") == null
				|| !PayCommonUtil.createSignStr("UTF-8", params, weixinAppKey).equals(params.get("sign"))) {
			throw new CustomException(EnumHandle.PAY_BACK_SIGN_INCORRECT);
		}
		Payment payment = iPaymentDao.queryPaymentByOrderId(params.get("out_trade_no").toString());
		// 先判断返回信息是否正确
		ValidateUtil.validateEmptyAndString(params.get("attach"), EnumHandle.PAY_BACK_PARAM_INCORRECT);
		if (!params.get("attach").equals(payment.getSioeyeSign())) {
			throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
		}
		// 设置支付时间
		payment.setPayTime(new Date());
		payment.setOpenId(params.get("openid"));
		// 处理payment支付成功
		if (!(iPaymentDao.update(payment) > 0)) {
			throw new CustomException(EnumHandle.UPDATE_PAYMENT_FAILED);
		}
		params.put("orderId", params.get("out_trade_no"));
		params.put("prepayId", payment.getPrepayId());
		// 回调小程序的时候需要的参数
		result.put("success", this.sendMqMessage(params));
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
		message.put("prepayId", map.get("prepayId"));
		message.put("openId", map.get("openid"));
		// 支付成功，向指定的mq消息队列中发送支付完成的消息
		Util.sendMqMessage(rabbitMqConfig.mqConnection, orderPaymentQueue, message.toJSONString());
		return true;
	}
}
