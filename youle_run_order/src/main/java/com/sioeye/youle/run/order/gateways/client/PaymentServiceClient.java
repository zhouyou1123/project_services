package com.sioeye.youle.run.order.gateways.client;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.payment.Payment;
import com.sioeye.youle.run.order.domain.payment.PaymentId;
import com.sioeye.youle.run.order.domain.payment.PaymentResult;
import com.sioeye.youle.run.order.interfaces.PaymentService;
import com.sioeye.youle.run.order.service.feign.IPaymentFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j
@Component
public class PaymentServiceClient implements PaymentService {

	@Autowired
	private IPaymentFeign paymentFeign;

	@Override
	public boolean validatePaid(String orderId, String paymentId, boolean isThirdQueryFlag) {

		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		map.put("paymentId", paymentId);
		map.put("orderStatus", 0);
		map.put("queryFlag", isThirdQueryFlag);

		try {
			String payment = paymentFeign.getPaymentDetail(map);
			JSONObject result = JSONObject.parseObject(payment);
			JSONObject paymentValueJsonObject = result.getJSONObject("value");
			if (!result.getBooleanValue("success")
					|| !"SUCCESS".equals(paymentValueJsonObject.getString("tradeState"))) {
				log.info("{" + "\"url\":\"pay/payDetail\"," + "\"orderId\":\"" + orderId + "\"," + "\"paymentId\":\""
						+ paymentId + "\"," + "\"error\":\"" + result.get("message") + "\"" + "}");
				return false;
			} else {
				return true;
			}
		} catch (Exception ex) {
			log.error("{" + "\"url\":\"pay/payDetail\"," + "\"orderId\":\"" + orderId + "\"," + "\"paymentId\":\""
					+ paymentId + "\"," + "\"error\":\"" + ex.getMessage() + "\"" + "}");
			throw new CustomException(DomainErrorCodeEnum.POST_PAY_STATUS_ERROR.getCode(),
					DomainErrorCodeEnum.POST_PAY_STATUS_ERROR.getMessage() + ex.getMessage());
		}
	}

	@Override
	public PaymentResult createPayment(Payment payment) {

		JSONObject map = new JSONObject();
		map.put("payWay", payment.getPayWay().getCode());
		map.put("tradeType", "APPLET_DISNEY");
		map.put("actualAmount", payment.getActualAmount());
		map.put("openId", payment.getBuyer().getOpenId());
		map.put("amusementParkId", payment.getAmusementParkId());
		map.put("orderId", payment.getOrderId());
		map.put("goodsName", payment.getGoodsName());
		map.put("goodsDesc", payment.getGoodsDesc());
		map.put("spbillCreateIp", payment.getSpbillCreateIp());

		String prePayment = paymentFeign.prePayment(map);

		map = JSONObject.parseObject(prePayment);
		if (!map.getBooleanValue("success")) {
			log.info("{" + "\"url\":\"pay/prePayment\"," + "\"orderId\":\"" + payment.getOrderId() + "\","
					+ "\"openId\":\"" + payment.getBuyer().getOpenId() + "\"," + "\"error\":\"" + map.get("message")
					+ "\"" + "}");
			throw new CustomException(map.getString("code"), map.getString("message"));
		}
		String timeStamp = null;
		String packageString = null;
		String paySign = null;
		String appId = null;
		String signType = null;
		String nonceStr = null;
		PaymentId paymentId = null;
		Buyer buyer = null;
		String prePaymentId = null;
		JSONObject value = (JSONObject) map.get("value");
		if (value.get("resultData") != null) {
			JSONObject resultData = (JSONObject) value.get("resultData");
			timeStamp = resultData.getString("timeStamp");
			packageString = resultData.getString("package");
			paySign = resultData.getString("paySign");
			appId = resultData.getString("appId");
			signType = resultData.getString("signType");
			nonceStr = resultData.getString("nonceStr");
		}
		PaymentResult paymentResult = new PaymentResult(timeStamp, packageString, paySign, appId, signType, nonceStr,
				new PaymentId(value.getString("objectId")), payment.getBuyer(), value.getString("prepayId"),
				value.getString("tradeType"), value.getInteger("payWay"));

		return paymentResult;
	}
}
