package com.sioeye.youle.run.pay.yeepay.config;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月28日
 *
 * @fileName EnumLedgerType.java
 *
 * @todo 分账类型
 */
public enum EnumLedgerType {

	AMOUNT(1), PROPORTION(2);

	private Integer code;

	EnumLedgerType(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
