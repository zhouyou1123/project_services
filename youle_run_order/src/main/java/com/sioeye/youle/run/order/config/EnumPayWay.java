package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.util.ValidateUtil;

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

	public static int validatePayWay(Object object, boolean defaultSioeyeEnablePayWay, Integer defaultSioeyePayWay) {
		int result = EnumPayWay.YEEPAY.getCode();
		if (defaultSioeyeEnablePayWay) {
			result = defaultSioeyePayWay;
		} else {
			if (object == null) {
				result = defaultSioeyePayWay;
			} else {
				ValidateUtil.validateEmptyAndInteger(object, EnumHandle.PLACE_ORDER_PAYWAY_PARAM_INCORRECT);
				boolean flag = false;
				for (EnumPayWay enumPayWay : EnumPayWay.values()) {
					if (enumPayWay.getCode() == Integer.parseInt(object.toString())) {
						result = enumPayWay.getCode();
						flag = true;
						break;
					}
				}
				if (!flag) {
					throw new CustomException(EnumHandle.PLACE_ORDER_PAYWAY_PARAM_INCORRECT);
				}
			}
		}
		return result;
	}
}
