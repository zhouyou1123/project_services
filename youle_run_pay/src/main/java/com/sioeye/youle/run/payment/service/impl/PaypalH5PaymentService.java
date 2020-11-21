package com.sioeye.youle.run.payment.service.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumPayWay;
import com.sioeye.youle.run.payment.config.EnumTradeType;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.service.intf.IPaypalH5Payment;
import com.sioeye.youle.run.payment.util.HttpUtil;
import com.sioeye.youle.run.payment.util.Util;
import com.sioeye.youle.run.payment.util.ValidateUtil;
import com.sioeye.youle.run.payment.wxpay.PayCommonUtil;

@Service
@RefreshScope
public class PaypalH5PaymentService implements IPaypalH5Payment {

	@Value("${sioeye.weixin.appletface.id}")
	private String sioeyeWeixinAppletId;// 小程序id(小程序和公众号用的是同一个wx账号)
	@Value("${sioeye.key}")
	private String sioeyeKey;
	@Value("${paypal.pay.url}")
	private String paypalPayUrl;
	@Autowired
	private IPaymentDao iPaymentDao;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		// 创建16位随机数
		String nonceStr = Util.createNoncestr(16);
		map.put("nonceStr", nonceStr);
		// 创建attach
		String sioeyeSign = PayCommonUtil.getSioeyeSign(System.currentTimeMillis(), map.get("orderId").toString(),
				sioeyeWeixinAppletId, nonceStr, sioeyeKey);
		map.put("sioeyeSign", sioeyeSign);
		// 调用paypal支付
		Payment payment = new Payment();
		JSONObject paypalParamJson = new JSONObject();
		paypalParamJson.put("totalFee", map.get("actualAmount"));
		paypalParamJson.put("currency", map.get("currency"));
		paypalParamJson.put("orderId", map.get("orderId"));
		paypalParamJson.put("paypalSuccessUrl", map.get("paypalSuccessUrl"));
		paypalParamJson.put("openId", map.get("openId"));
		paypalParamJson.put("tradeType", map.get("tradeType"));
		String paypalStr = HttpUtil.httpsRequest(paypalPayUrl, "POST", paypalParamJson.toJSONString(),
				"application/json");
		JSONObject paypalJson = JSONObject.parseObject(paypalStr);
		ValidateUtil.validateRemoteCall("paypal_pay_url", paypalJson, EnumHandle.CALL_PAYPAL_FAILED);
		if (paypalJson.getBoolean("success")) {
			JSONObject paypalJsonValue = paypalJson.getJSONObject("value");
			map.put("paypalUrl", paypalJsonValue.getString("paypalUrl"));
			// 调用插入payment方法
			this.insertPayment(payment, map, result);
			return result;
		}
		throw new CustomException(EnumHandle.WEIXIN_RETURN_PARAMS_ERROR);
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception {
		// TODO Auto-generated method stub
		return null;
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
	private void insertPayment(Payment payment, Map<String, Object> map, JSONObject result)
			throws CustomException, Exception {
		// 1：微信支付
		payment.setOpenId(map.get("openId").toString());
		payment.setPayWay(EnumPayWay.PAYPAL.getCode());
		payment.setNonceStr(map.get("nonceStr").toString());
		payment.setTradeType(EnumTradeType.H5_LINE_PAYPAL_DISNEY.toString());
		payment.setSioeyeSign(map.get("sioeyeSign").toString());
		payment.setUpdateTime(new Date());
		if (!(iPaymentDao.save(payment) > 0)) {
			throw new CustomException(EnumHandle.INSERT_PAYMENT_FAILED);
		}
		result.put("paypalUrl", map.get("paypalUrl").toString());
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
	}
}
