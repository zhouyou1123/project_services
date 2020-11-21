package com.sioeye.youle.run.order.config;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年6月5日
 * EnumOrdersStatus.java description 订单状态
 */
public enum EnumOrdersStatus {

	UN_ORDER(-1, "unorder"), UN_PAID(0, "unpaid"), PAY_SUCCESS(1, "pay success");

	private Integer code;
	private String message;

	EnumOrdersStatus(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static EnumOrdersStatus valueOf(Integer code) {
		for (EnumOrdersStatus status : values()) {
			if (status.getCode().equals(code)) {
				return status;
			}
		}
		return EnumOrdersStatus.UN_ORDER;
	}

	public static EnumOrdersStatus valueOfString(String code) {
		for (EnumOrdersStatus status : values()) {
			if (status.getCode().compareTo(Integer.parseInt(code)) == 0) {
				return status;
			}
		}
		return EnumOrdersStatus.UN_ORDER;
	}
}
