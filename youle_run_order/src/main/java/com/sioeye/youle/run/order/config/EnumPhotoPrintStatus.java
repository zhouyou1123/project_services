package com.sioeye.youle.run.order.config;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年8月12日
 *
 * @fileName EnumPhotoPrintStatus.java
 *
 * @todo 照片打印状态
 */
public enum EnumPhotoPrintStatus {

	UN_PAY(0), PAID_UN_MOVE(1), PAID_MOVED(2), PAID_PRINT(3);
	private Integer code;

	EnumPhotoPrintStatus(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
