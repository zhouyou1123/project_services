package com.sioeye.youle.run.payment.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.EnumTradeType;
import com.sioeye.youle.run.payment.service.intf.IPayment;
import com.sioeye.youle.run.payment.service.intf.IPaypalH5Payment;
import com.sioeye.youle.run.payment.service.intf.IPaypalPayment;

@Service
@RefreshScope
public class PaypalPaymentService implements IPaypalPayment {

	@Autowired
	private IPaypalH5Payment iPaypalH5Payment;

	@Override
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception {
		// 获取对应服务
		IPayment iPayment = this.getService(map.get("tradeType").toString());
		// 调用对应方法
		return iPayment.prePayment(map);
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
	 * 根据交易类型获取对应的实现类
	 * 
	 * @param tradeType
	 * @return
	 */
	private IPayment getService(String tradeType) {
		if (EnumTradeType.H5_LINE_PAYPAL_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			return iPaypalH5Payment;
		} else if (EnumTradeType.H5_FACEBOOK_PAYPAL_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			return iPaypalH5Payment;
		}
		throw new CustomException(EnumHandle.TRADE_TYPE_INCORRECT);
	}
}
