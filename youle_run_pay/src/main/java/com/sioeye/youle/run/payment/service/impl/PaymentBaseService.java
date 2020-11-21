package com.sioeye.youle.run.payment.service.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumPayWay;
import com.sioeye.youle.run.payment.controller.PaymentController;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.service.intf.IPayment;
import com.sioeye.youle.run.payment.service.intf.IPaymentBase;
import com.sioeye.youle.run.payment.service.intf.IPaypalPayment;
import com.sioeye.youle.run.payment.service.intf.IWXPayment;
import com.sioeye.youle.run.payment.service.intf.IYeepayPayment;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName PaymentBaseService.java
 *
 * @todo 支付基础实现类
 */
@Service
@RefreshScope
public class PaymentBaseService implements IPaymentBase {

	@Value("${sioeye.weixin.appletface.id}")
	private String sioeyeWeixinAppletId;// 小程序id(小程序和公众号用的是同一个wx账号)
	@Autowired
	private IWXPayment iWXPayment;
	@Autowired
	private IPaypalPayment iPaypalPayment;
	@Autowired
	private IYeepayPayment iYeepayPayment;
	@Autowired
	private IPaymentDao iPaymentDao;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		// 根据支付方式获取支付实现类
		IPayment iPayment = this.getPaymentService(Integer.parseInt(map.get("payWay").toString()));
		// 调用对应支付类的预支付方法
		return iPayment.prePayment(map);
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		// 获取payment信息
		Payment payment = iPaymentDao.getPaymentDetail(map.get("paymentId").toString());
		if (payment == null) {
			throw new CustomException(EnumHandle.NOT_FOUND_PAYMENT);
		}
		// 如果是免费订单，直接返回支付信息
		if ("disney_free_payment".equals(payment.getPrepayId()) || payment.getPayTime() != null) {
			this.packageResult(result, payment);
			return result;
		}
		// 根据支付方式获取支付实现类
		IPayment iPayment = this.getPaymentService(payment.getPayWay());
		// 判断是否进行第三方支付查询
		boolean queryFlag = (boolean) map.get("queryFlag");
		if (queryFlag) {
			// 设置交易类型
			map.put("tradeType", payment.getTradeType());
			// 调用对应支付类的预支付方法
			result = iPayment.getPaymentDetail(map);
			// 判断第三方是否支付成功
			if ("SUCCESS".equals(result.getString("tradeState")) && payment.getPayTime() == null) {
				// 第三方支付成功且本地未支付
				payment.setPayTime(new Date());
				if (!(iPaymentDao.update(payment) > 0)) {
					throw new CustomException(EnumHandle.UPDATE_PAYMENT_FAILED);
				}
			}
		}
		// 封装返回值
		this.packageResult(result, payment);
		return result;
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
		result.put("nonceStr", payment.getNonceStr());
		result.put("appId", sioeyeWeixinAppletId);
	}

	@Override
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception {
		// 根据支付方式获取支付实现类
		IPayment iPayment = this.getPaymentService(Integer.parseInt(map.get("payWay")));
		// 调用对应支付类的预支付方法
		map.remove("payWay");
		return iPayment.payBack(map);
	}

	/**
	 * 根据支付方式，返回支付实现类
	 * 
	 * @param payWay
	 * @return
	 */
	private IPayment getPaymentService(int payWay) {
		if (payWay == EnumPayWay.WEIXIN.getCode()) {
			// 微信支付
			return iWXPayment;
		} else if (payWay == EnumPayWay.PAYPAL.getCode()) {
			return iPaypalPayment;
		} else if (payWay == EnumPayWay.YEEPAY.getCode()) {
			return iYeepayPayment;
		}
		throw new CustomException(EnumHandle.PAY_WAY_INCORRECT);
	}
}
