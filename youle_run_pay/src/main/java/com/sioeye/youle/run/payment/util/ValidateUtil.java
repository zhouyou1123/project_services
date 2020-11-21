package com.sioeye.youle.run.payment.util;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年10月22日
 *
 * @fileName ValidateUtil.java
 *
 * @todo 验证参数工具类
 */

public class ValidateUtil {

	private static final Log logger = LogFactory.getLog(ValidateUtil.class);

	/**
	 * 验证对象是否为空和布尔值
	 * 
	 * @param object
	 * @param enumHandle
	 *            void
	 */
	public static void validateEmptyAndBoolean(Object object, EnumHandle enumHandle) {
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(enumHandle));
		if (!(object instanceof Boolean)) {
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 验证对象是否为空和字符串
	 * 
	 * @param object
	 * @param enumHandle
	 *            void
	 */
	public static void validateEmptyAndString(Object object, EnumHandle enumHandle) {
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(enumHandle));
		if (!(object instanceof String)) {
			throw new CustomException(enumHandle);
		}
		if ("".equals(object)) {
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 验证对象是否为空和列表
	 * 
	 * @param object
	 * @param enumHandle
	 *            void
	 */
	public static void validateEmptyAndList(Object object, EnumHandle enumHandle) {
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(enumHandle));
		if (object.getClass() != ArrayList.class) {
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 验证对象是否为空和整数
	 * 
	 * @param object
	 * @param enumHandle
	 *            void
	 */

	public static void validateEmptyAndInteger(Object object, EnumHandle enumHandle) {
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(enumHandle));
		Pattern pattern = Pattern.compile("^[1-9]|[1-9]+[0-9]*$");
		Matcher isNum = pattern.matcher(object.toString());
		if (!isNum.matches()) {
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 判断对象是否为空或者不是价格
	 * 
	 * @param obj
	 * @return
	 */
	public static void validateEmptyAndPrice(Object object, EnumHandle enumHandle) {
		Optional.ofNullable(object).orElseThrow(() -> new CustomException(enumHandle));
		Pattern pattern = Pattern.compile("0|0.0|(?!^0*(\\.0{1,2})?$)^\\d{1,13}(\\.\\d{1,2})?$");
		Matcher isNum = pattern.matcher(object.toString());
		if (!isNum.matches()) {
			throw new CustomException(enumHandle);
		}
	}

	/**
	 * 验证远程过程调用返回值
	 * 
	 * @param jsonObject
	 * @param enumHandle
	 * @throws CustomException
	 *             void
	 */
	public static void validateRemoteCall(String handle, JSONObject jsonObject, EnumHandle enumHandle)
			throws CustomException {
		Optional.ofNullable(jsonObject).orElseThrow(() -> new CustomException(enumHandle));
		logger.info(handle + " return value :" + jsonObject);
		if (!jsonObject.getBooleanValue("success")) {
			JSONObject jsonObjectValue = jsonObject.getJSONObject("value");
			if (jsonObjectValue == null) {
				// 如果为空，直接获取code和message
				enumHandle.setCode(jsonObject.getString("code"));
				enumHandle.setMessage(jsonObject.getString("message"));
			} else {
				// 不为空，那么获取value中的code和message
				enumHandle.setCode(jsonObjectValue.getString("code"));
				enumHandle.setMessage(jsonObjectValue.getString("message"));
			}
			throw new CustomException(enumHandle);
		}
	}
}
