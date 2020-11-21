package com.sioeye.youle.run.payment.config;

/**
 * author zhouyou 
 * ckt email:jinx.zhou@ck-telecom.com
 * 2017年5月26日
 * EnumHandle.java 
 * description 错误枚举类
 */
public enum EnumHandle {

	INTERNAL_ERROR("110600","Internal Error . "),
	INSERT_PAYMENT_FAILED("110601","insert into payment is failure . "),
	PAY_WAY_INCORRECT("110602","pay way is incorrect . "),
	TRADE_TYPE_INCORRECT("110603","trade type is incorrect . "),
	HTTP_URL_IS_INCORRECT("110604","url is incorrect . "),
	WEIXIN_UNIFIED_ORDER_FAILED("110605","call weixin unified order is failed . "),
	WEIXIN_RETURN_PARAMS_ERROR("110606","weixin unified order return params is error . "),
	NOT_FOUND_PAYMENT("110607","payment is not exist . "),
	QUERY_ORDER_FAILED("110608","query order is failed . "),
	UPDATE_PAYMENT_FAILED("110609","update payment is failed . "),
	PAY_BACK_PARAM_INCORRECT("110610","pay back params is incorrect . "),
	PAY_BACK_SIGN_INCORRECT("110611","pay back sign is incorrect . "),
	PAY_BACK_WEIXIN_FAILED("110612","pay back weixin is failed . "),
	PARAMS_ERROR("110613","params is error . "),
	CALL_PAYPAL_FAILED("110614","call paypal server pay is failure . "),
	SEND_WEIXIN_PAY_MESSAGE_FAILED("110615","send weixin pay message is failure . "),
	CALL_YEEPAY_FAILURE("110616","call yeepay is failure . "),
	PAY_BACK_YEEPAY_FAILED("110617","pay back yeepay is failed . "),
	YEEPAY_RETURN_DATE_PACKAGE_ERROR("110618","yeepay return date package prepayid is error . "),
	YEEPAY_RETURN_DATE_ERROR("110619","yeepay return date is error . ");

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
