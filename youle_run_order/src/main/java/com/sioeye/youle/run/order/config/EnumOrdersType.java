package com.sioeye.youle.run.order.config;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年6月5日
 * EnumOrdersStatus.java description 订单状态
 */
public enum EnumOrdersType {

	FULL(0), DISCOUNT(1), COUPON(2), SHARE(3);

	private Integer code;

	EnumOrdersType(Integer code) {
        this.code = code;
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
