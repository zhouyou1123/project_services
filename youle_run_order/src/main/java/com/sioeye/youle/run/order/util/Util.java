package com.sioeye.youle.run.order.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月4日
 *
 * @fileName Util.java
 *
 * @todo 工具类
 */
public class Util {

	
	/**
	 * utc时间转换为当前时间
	 * 
	 * @param utcTime
	 * @return Date
	 */
	public static String utcToLocal(Date utcDate, String localTimeZone) {
		TimeZone timeZone = TimeZone.getTimeZone(localTimeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(timeZone);
		return simpleDateFormat.format(new Date());
	}

	/**
	 * 获取MD5值
	 * 
	 * @param plainText
	 * @return
	 * @throws NoSuchAlgorithmException
	 *             String
	 */
	public static String HEXAndMd5(String plainText) throws NoSuchAlgorithmException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(plainText.getBytes("UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer(200);
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset] & 0xff;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param obj
	 * @return boolean
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}

	/**
	 * 获取UTC时间
	 * 
	 * @return String
	 */
	public static Calendar getCalendar() {
		// 1、取得本地时间：
		return Calendar.getInstance();
	}

	/**
	 * 验证对象是否是时间戳
	 * 
	 * @param object
	 * @return boolean
	 */
	public static boolean validateObjectTimeStamp(Object object) {
		if (object == null || (object.toString().length() != 13 && object.toString().length() != 10)) {
			return false;
		}
		return true;
	}

	/**
	 * 时间戳转date
	 * 
	 * @param object
	 * @return
	 * @throws ParseException
	 *             Date
	 */
	public static Date timeStamp2Date(Object object) throws ParseException {
		if (object != null) {
			if (!Util.validateObjectTimeStamp(object)) {
				throw new CustomException(EnumHandle.PARAMS_INCORRECT);
			}
			Long timeStamp = Long.parseLong(object.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (timeStamp.toString().length() == 10) {
				timeStamp = timeStamp * 1000;
			}
			String d = format.format(timeStamp);
			Date date = format.parse(d);
			Calendar calendar = Util.getCalendar();
			calendar.setTime(date);
			return calendar.getTime();
		} else {
			throw new CustomException(EnumHandle.PARAMS_INCORRECT);
		}
	}

	/**
	 * 发送mq消息
	 * 
	 * @param mqConnection
	 * @param queueName
	 * @param message
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void sendMqMessage(Connection mqConnection, String queueName, String message)
			throws IOException, TimeoutException {
		// 创建一个通道
		Channel channel = mqConnection.createChannel();
		// 发送消息到队列中
		channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
		// 关闭通道和连接
		channel.close();
	}
	/**
	 * 通过exchange发送消息
	 * 
	 * @param mqConnection
	 * @param queueName
	 * @param message
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void sendExchangeMessage(Connection mqConnection, String exchange,String routingKey, String message)
			throws IOException, TimeoutException {
		// 创建一个通道
		Channel channel = mqConnection.createChannel();
		// 发送消息到exchange中
		channel.basicPublish(exchange, routingKey, null, message.getBytes("UTF-8"));
		// 关闭通道和连接
		channel.close();
	}
	
	/**
	 * 
	 * @Description:封装接口请求成功的数据
	 * @Author GuoGongwei
	 * @Time: 2019年9月16日下午5:46:44
	 * @param value
	 * @return
	 */
	public static JSONObject returnSuccessUtils(Object value) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("value", value);
		return result;
	}
	
	public static StringBuilder formateTime(Date time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		String date = format.format(time);
		StringBuilder build = new StringBuilder(date); 
		return build;
	}
}
