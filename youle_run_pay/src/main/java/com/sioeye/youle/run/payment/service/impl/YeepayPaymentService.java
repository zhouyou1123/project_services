package com.sioeye.youle.run.payment.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumTradeType;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.service.intf.IPayment;
import com.sioeye.youle.run.payment.service.intf.IYeepayAppletPayment;
import com.sioeye.youle.run.payment.service.intf.IYeepayPayment;
import com.sioeye.youle.run.payment.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName YeepayPaymentService.java
 *
 * @todo 易宝支付实现类
 */
@Service
@RefreshScope
public class YeepayPaymentService implements IYeepayPayment {

	@Autowired
	private IYeepayAppletPayment iYeepayAppletPayment;
	@Autowired
	private IPaymentDao iPaymentDao;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		// 获取对应服务
		IPayment iPayment = this.getService(map.get("tradeType").toString());
		// 调用对应方法
		return iPayment.prePayment(map);
	}

	@Override
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception {
		// 获取对应服务
		IPayment iPayment = this.getService(map.get("tradeType").toString());
		// 调用对应方法
		return iPayment.getPaymentDetail(map);
	}

	@Override
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception {
		Optional.ofNullable(map.get("orderId"))
				.orElseThrow(() -> new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT));
		// 查询出支付信息
		Payment payment = iPaymentDao.queryPaymentByOrderId(map.get("orderId").toString());
		if (payment == null) {
			throw new CustomException(EnumHandle.NOT_FOUND_PAYMENT);
		}
		if (payment != null && payment.getPayTime() != null && !"".equals(payment.getPayTime())) {
			// 封装返回对象
			JSONObject result = (JSONObject) JSON.toJSON(payment);
			return result;
		}
		// 根据交易类型获取对应的服务
		IPayment iPayment = this.getService(payment.getTradeType());
		JSONObject payBackJson = iPayment.payBack(map);
		ValidateUtil.validateRemoteCall("pay back yeepay", payBackJson, EnumHandle.PAY_BACK_YEEPAY_FAILED);
		// 封装返回对象
		JSONObject result = (JSONObject) JSON.toJSON(payment);
		return result;
	}

	/**
	 * 根据交易类型获取对应的实现类
	 * 
	 * @param tradeType
	 * @return
	 */
	private IPayment getService(String tradeType) {
		if (EnumTradeType.APPLET_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			// 易宝支付小程序
			return iYeepayAppletPayment;
		} else if (EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			// 易宝支付集锦
			return iYeepayAppletPayment;
		} else if (EnumTradeType.APPLET_PHOTO.toString().equalsIgnoreCase(tradeType)) {
			// 易宝支付小程序照片
			return iYeepayAppletPayment;
		} else {
			return iYeepayAppletPayment;
		}
	}
}
