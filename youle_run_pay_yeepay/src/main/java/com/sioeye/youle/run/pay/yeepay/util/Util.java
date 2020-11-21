package com.sioeye.youle.run.pay.yeepay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年5月26日 UtilTool.java
 * description 工具类
 */
public class Util {

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
}