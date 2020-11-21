package com.sioeye.youle.run.payment.config;

/**
 * 
 * @author zhouyou
 * @cktEmail:jinx.zhou@ck-telecom.com
 * @date 2018年1月16日
 * @fieleName EnumPayWay.java
 * @TODO 支付方式
 */
public enum EnumPayWay {

	WEIXIN(1), ALI(2), IOS(3), COUPON(4), LINE(5), PAYPAL(6), YEEPAY(7);

	private Integer code;

	EnumPayWay(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
