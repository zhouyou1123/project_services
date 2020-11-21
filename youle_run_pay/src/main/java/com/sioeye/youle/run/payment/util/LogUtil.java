package com.sioeye.youle.run.payment.util;

import org.apache.commons.logging.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;

public class LogUtil {

	/**
	 * 成功日志
	 * 
	 * @param logger
	 * @param object
	 *            对象
	 * @return
	 */
	public static String packageSuccessLog(Log logger, Object object) {
		String result = "";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		if (object != null) {
			jsonObject.put("value", "{#value}");
			String jsonStr = JSON.toJSONString(object);
			result = jsonObject.toString().replace("\"{#value}\"", jsonStr);
		} else {
			result = jsonObject.toString();
		}
		logger.info(result);
		return result;
	}

	/**
	 * 服务器内部错误
	 * 
	 * @param e
	 * @return JSONObject
	 */
	public static JSONObject internalError(Log logger, Throwable e, Object parameter) {
		// 返回值
		StackTraceElement[] elements = e.getStackTrace();
		StackTraceElement element = null;
		if (element == null) {
			element = elements[elements.length - 1];
		}
		String message = e.getMessage();
		if (message == null || "".equals(message)) {
			message = e.getLocalizedMessage();
		}
		// 设置错误
		JSONObject errorMessage = new JSONObject();
		errorMessage.put("type", "Internal Server Error");
		errorMessage.put("message", message);
		errorMessage.put("class", element.getClassName());
		errorMessage.put("method", element.getMethodName());
		errorMessage.put("lineNumber", element.getLineNumber());
		errorMessage.put("parameter", parameter);
		// 打印日志
		logger.error(errorMessage);
		return errorMessage;
	}

	/**
	 * 业务错误
	 * 
	 * @param e
	 * @param requestParameter
	 * @return JSONObject
	 */
	public static JSONObject BusinessError(Log logger, CustomException e) {
		// 设置错误
		JSONObject errorMessage = new JSONObject();
		errorMessage.put("success", false);
		errorMessage.put("code", e.getCode());
		errorMessage.put("message", e.getMessage());
		// 打印日志
		logger.error(errorMessage);
		return errorMessage;
	}
}
