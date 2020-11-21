package com.sioeye.youle.run.payment.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月26日 UtilTool.java
 * description 工具类
 */
public class Util {

	/**
	 * 创建订单交易结束时间
	 * 
	 * @param timeZone
	 * @param second
	 * @return String
	 */
	public static String createTimeExpire(String timeZone, int second) {
		TimeZone zone = TimeZone.getTimeZone(timeZone);
		Calendar calendar = Calendar.getInstance(zone);
		calendar.add(Calendar.SECOND, second);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(zone);
		return sdf.format(calendar.getTime());
	}
	/**
	 * 将json值转换为map
	 * 
	 * @param jsonObject
	 * @return Map<String,String>
	 */
	public static Map<String, String> json2Map(JSONObject jsonObject) {
		Map<String, String> result = new HashMap<>();
		Set<String> keySet = jsonObject.keySet();
		for (String key : keySet) {
			result.put(key, jsonObject.getString(key));
		}
		return result;
	}

	/**
	 * 
	 * @param length
	 * @return
	 */
	public static String createNoncestr(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < length; i++) {
			Random rd = new Random();
			res.append(chars.charAt(rd.nextInt(chars.length() - 1)));
		}
		return res.toString();
	}

	/**
	 * 判断是否是xml结构
	 */
	public static boolean isXML(String value) {
		try {
			DocumentHelper.parseText(value);
		} catch (DocumentException e) {
			return false;
		}
		return true;
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
		mqConnection.close();
	}
}