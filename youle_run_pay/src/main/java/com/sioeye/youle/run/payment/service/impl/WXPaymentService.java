package com.sioeye.youle.run.payment.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.sioeye.youle.run.payment.service.intf.IWXAppletPayment;
import com.sioeye.youle.run.payment.service.intf.IWXH5Payment;
import com.sioeye.youle.run.payment.service.intf.IWXHighlightPayment;
import com.sioeye.youle.run.payment.service.intf.IWXPayment;
import com.sioeye.youle.run.payment.service.intf.IWXScanPayment;
import com.sioeye.youle.run.payment.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName WXPaymentService.java
 *
 * @todo 微信支付实现类
 */
@Service
@RefreshScope
public class WXPaymentService implements IWXPayment {

	@Autowired
	private IWXScanPayment iWXScanPayment;
	@Autowired
	private IWXAppletPayment iWXAppletPayment;
	@Autowired
	private IWXHighlightPayment iWXHighlightPayment;
	@Autowired
	private IWXH5Payment iWXH5Payment;
	@Autowired
	private IPaymentDao iPaymentDao;
	@Value("${sioeye.weixin.appletface.key}")
	private String sioeyeWeixinAppletKey;// 小程序key

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
		// 判断返回值是否为空
		Optional.ofNullable(map.get("out_trade_no"))
				.orElseThrow(() -> new CustomException(EnumHandle.PAY_BACK_PARAM_INCORRECT));
		// 查询出支付信息
		Payment payment = iPaymentDao.queryPaymentByOrderId(map.get("out_trade_no").toString());
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
		ValidateUtil.validateRemoteCall("pay back weixin", payBackJson, EnumHandle.PAY_BACK_WEIXIN_FAILED);
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
			// 微信小程序支付查询
			return iWXAppletPayment;
		} else if (EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			// 微信集锦支付
			return iWXHighlightPayment;
		} else if (EnumTradeType.H5_APPLET_DISNEY.toString().equalsIgnoreCase(tradeType)) {
			return iWXH5Payment;
		} else if (EnumTradeType.APPLET_PHOTO.toString().equalsIgnoreCase(tradeType)) {
			return iWXAppletPayment;
		} else if (EnumTradeType.H5_TERMINAL_PHOTO.toString().equalsIgnoreCase(tradeType)) {
			return iWXScanPayment;
		} else {
			throw new CustomException(EnumHandle.TRADE_TYPE_INCORRECT);
		}
	}

}
