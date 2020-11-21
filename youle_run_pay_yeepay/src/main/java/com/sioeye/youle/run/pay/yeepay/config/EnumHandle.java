package com.sioeye.youle.run.pay.yeepay.config;

/**
 * author zhouyou 
 * ckt email:jinx.zhou@ck-telecom.com
 * 2017年5月26日
 * EnumHandle.java 
 * description 错误枚举类
 */
public enum EnumHandle {

	INTERNAL_ERROR("110900","Internal Error . "),
	YEEPAY_TRADE_ORDER_FAILURE("110901","yeepay trade order is failure . "),
	YEEPAY_API_PAY_FAILURE("110902","yeepay api pay is failure . "),
	SAVE_YEEPAY_FAILURE("110903","save yeepay is failure . "),
	SAVE_LEDGERDETAIL_FAILURE("110904","save ledgerdetail is failure . "),
	PARAMS_ORDERID_ERROR("110905","params orderid is error . "),
	PARAMS_AMOUNT_ERROR("110906","params order amount is error . "),
	PARAMS_GOODSNAME_ERROR("110907","params goods name is error . "),
	PARAMS_GOODSDESC_ERROR("110908","params goods description is error . "),
	PARAMS_AMUSEMENTPARKID_ERROR("110909","params amusementParkId is error . "),
	PARAMS_OPENID_ERROR("110910","params openId is error . "),
	PARAMS_USERIP_ERROR("110911","params userIp is error . "),
	LEDGER_TYPE_ERROR("110912","ledger detail type is error . "),
	TRADE_ORDER_PREPAYID_ERROR("110913","yeepay trade order prepayid is error . "),
	PARAMS_PAYMENTID_ERROR("110914","params paymentId is error . "),
	YEEPAY_NOT_EXIST("110915","yeepay is not exist . "),
	YEEPAY_QUERY_ORDER_FAILURE("110916","yeepay query order is failure . "),
	CALL_PARK_PLANS_ERROR("110917","call park plans is error . "),
	CALL_PARK_PLANS_RESULT_ERROR("110918","call park plans result is error . "),
	LEDGER_AMOUNT_ERROR("110919","ledger amount is error . ");

	private String code;
	private String message;

	EnumHandle(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
