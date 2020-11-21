package com.sioeye.youle.run.payment.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import com.sioeye.youle.run.payment.service.feign.IYeepayFeign;
import com.sioeye.youle.run.payment.service.intf.IYeepayAppletPayment;
import com.sioeye.youle.run.payment.util.ConstUtil;
import com.sioeye.youle.run.payment.util.Util;
import com.sioeye.youle.run.payment.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName YeepayAppletPaymentService.java
 *
 * @todo 易宝小程序支付
 */
@Service
@RefreshScope
public class YeepayAppletPaymentService implements IYeepayAppletPayment {

	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Value("${sioeye.weixin.appletface.id}")
	private String sioeyeWeixinAppletId;// 小程序id(小程序和公众号用的是同一个wx账号)
	@Autowired
	private IYeepayFeign iYeepayFeign;
	@Autowired
	private IPaymentDao iPaymentDao;
	@Autowired
	private RabbitMqConfig rabbitMqConfig;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		// 返回值
		Payment payment = new Payment();
		// 如果总费用为0(使用优惠劵),那么不需要调用微信，直接创建一个免费订单,并将prepay_id设置为disney_free_payment
		if (this.validatePrice(map, payment)) {
			return this.packageResult(null, payment);
		}
		this.insertPayment(map, payment);
		// 调用易宝支付接口
		map.put("paymentId", payment.getObjectId());
		String yeepayStr = iYeepayFeign.preYeepay(map);
		JSONObject jsonYeepay = JSONObject.parseObject(yeepayStr);
		ValidateUtil.validateRemoteCall("pre_yeepay", jsonYeepay, EnumHandle.CALL_YEEPAY_FAILURE);
		JSONObject jsonYeepayValue = jsonYeepay.getJSONObject("value");
		// 更新支付payment信息
		return this.updatePayment(map, jsonYeepayValue, payment);
	}

	private boolean validatePrice(Map<String, Object> map, Payment payment) throws CustomException, Exception {
		if (Double.parseDouble(map.get("actualAmount").toString()) == 0) {
			payment.setPrepayId(ConstUtil.DISNEY_FREE_PAYMENT.toString());
			payment.setNonceStr(Util.createNoncestr(16));
			payment.setPayTime(new Date());
			// 调用插入payment方法
			this.insertPayment(map, payment);
			return true;
		}
		return false;
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		// 返回值
		JSONObject result = new JSONObject();
		String yeepayDetailStr = iYeepayFeign.getYeepayDetail(map);
		JSONObject yeepayDetailJson = JSONObject.parseObject(yeepayDetailStr);
		ValidateUtil.validateRemoteCall("get_yeepay_detail", yeepayDetailJson, EnumHandle.CALL_YEEPAY_FAILURE);
		JSONObject yeepayDetailValue = yeepayDetailJson.getJSONObject("value");
		// 查询易宝的支付结果
		result.put("tradeState", yeepayDetailValue.getString("tradeState"));
		result.put("tradeStateDesc", yeepayDetailValue.get("tradeStateDesc"));
		result.put("appId", sioeyeWeixinAppletId);
		// 第三方支付已完成，并且当前订单还是未支付
		if ("SUCCESS".equals(yeepayDetailValue.getString("tradeState"))
				&& Integer.parseInt(map.get("orderStatus").toString()) != EnumOrdersStatus.PAY_SUCCESS.getCode()) {
			Map<String, String> mqMap = new HashMap<>();
			mqMap.put("orderId", map.get("orderId").toString());
			this.sendMqMessage(mqMap);
		}
		return result;
	}

	@Override
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		if (!"SUCCESS".equalsIgnoreCase(map.get("status"))) {
			throw new CustomException(EnumHandle.PAY_BACK_YEEPAY_FAILED);
		}
		// 判断价格是否正确
		String actualAmountStr = iPaymentDao.queryActualAmountByOrderId(map.get("orderId").toString());
		ValidateUtil.validateEmptyAndString(actualAmountStr, EnumHandle.PAY_BACK_PARAM_INCORRECT);
		BigDecimal promoteTotal = new BigDecimal(actualAmountStr);
		if (map.get("payAmount") == null || "".equals(map.get("payAmount"))
				|| !map.get("payAmount").equals(promoteTotal.toString())) {
			throw new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT);
		}
		Payment payment = iPaymentDao.queryPaymentByOrderId(map.get("orderId").toString());
		// 设置支付时间
		payment.setPayTime(new Date());
		// 处理payment支付成功
		if (!(iPaymentDao.update(payment) > 0)) {
			throw new CustomException(EnumHandle.UPDATE_PAYMENT_FAILED);
		}
		map.put("orderId", map.get("orderId"));
		map.put("openId", payment.getOpenId());
		map.put("prepayId", payment.getPrepayId());
		// 回调小程序的时候需要的参数
		result.put("success", this.sendMqMessage(map));
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
	private void insertPayment(Map<String, Object> map, Payment payment) throws CustomException, Exception {
		// 7：易宝支付
		payment.setPayWay(EnumPayWay.YEEPAY.getCode());
		if (map.get("openId")!=null){
			payment.setOpenId(map.get("openId").toString());
		}
		if (map.get("tradeType") == null) {
			payment.setTradeType(EnumTradeType.APPLET_DISNEY.toString());
		} else {
			payment.setTradeType(map.get("tradeType").toString());
		}
		payment.setUpdateTime(new Date());
		if (!(iPaymentDao.save(payment) > 0)) {
			throw new CustomException(EnumHandle.INSERT_PAYMENT_FAILED);
		}
	}

	/**
	 * 更新支付信息
	 * 
	 * @param map
	 * @param jsonYeepayValue
	 * @param payment
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             JSONObject
	 */

	private JSONObject updatePayment(Map<String, Object> map, JSONObject jsonYeepayValue, Payment payment)
			throws CustomException, Exception {
		JSONObject resultDataJson = jsonYeepayValue.getJSONObject("resultData");
		if (resultDataJson == null) {
			throw new CustomException(EnumHandle.YEEPAY_RETURN_DATE_ERROR);
		}
		payment.setSign(resultDataJson.getString("paySign"));
		payment.setNonceStr(resultDataJson.getString("nonceStr"));
		// 获取prepayid
		String prePayIdPackage = resultDataJson.getString("package");
		if (prePayIdPackage == null || "".equals(prePayIdPackage)) {
			throw new CustomException(EnumHandle.YEEPAY_RETURN_DATE_PACKAGE_ERROR);
		}
		String[] prePayIds = prePayIdPackage.split("=");
		if (prePayIds == null || prePayIds.length != 2) {
			throw new CustomException(EnumHandle.YEEPAY_RETURN_DATE_PACKAGE_ERROR);
		}
		payment.setPrepayId(prePayIds[1]);
		payment.setUpdateTime(new Date());
		if (!(iPaymentDao.update(payment) > 0)) {
			throw new CustomException(EnumHandle.INSERT_PAYMENT_FAILED);
		}
		// 封装返回值
		return this.packageResult(resultDataJson, payment);
	}

	/**
	 * 封装返回值
	 * 
	 * @param result
	 * @param payment
	 */
	private JSONObject packageResult(JSONObject resultDataJson, Payment payment) {
		JSONObject result = new JSONObject();
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
		result.put("resultData", resultDataJson);
		result.put("appId", sioeyeWeixinAppletId);
		return result;
	}
}
