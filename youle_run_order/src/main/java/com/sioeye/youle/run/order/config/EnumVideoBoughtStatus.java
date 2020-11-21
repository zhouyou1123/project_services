package com.sioeye.youle.run.order.config;

/**
 * 
 * @author zhouyou email jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月7日
 * 
 * @ClassName EnumVideoBoughtStatus.java
 * 
 * @Version v2.0.1
 *
 * @Todo
 *
 */
public enum EnumVideoBoughtStatus {
	UN_PAID(0, "unpaid"), PAID_UN_MOVE(1, "paid and no move"), PAID_MOVED(2, "paid and moved");

	private Integer code;
	private String message;

	EnumVideoBoughtStatus(Integer code, String message) {
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

}
