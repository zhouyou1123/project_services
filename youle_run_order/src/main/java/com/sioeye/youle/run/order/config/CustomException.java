package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月26日
 * CustomException.java description 自定义异常类
 */

public class CustomException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;// 错误码
	private String message;// 错误消息

	public CustomException() {

	}

	public CustomException(String code, String message) {
		super(code+":"+message);
		this.code = code;
		this.message = message;
	}

	public CustomException(EnumHandle enumHandle) {
		this.code = enumHandle.getCode();
		this.message = enumHandle.getMessage();
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

	public static CustomException build(DomainErrorCodeEnum domainErrorCodeEnum){
		return new CustomException(domainErrorCodeEnum.getCode(),domainErrorCodeEnum.getMessage());
	}
}
