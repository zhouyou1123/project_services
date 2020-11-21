package com.sioeye.youle.run.payment.service.receiver;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sioeye.youle.run.payment.config.CustomException;
import com.sioeye.youle.run.payment.config.EnumHandle;
import com.sioeye.youle.run.payment.config.RabbitMqConfig;
import com.sioeye.youle.run.payment.dao.IPaymentDao;
import com.sioeye.youle.run.payment.model.Payment;
import com.sioeye.youle.run.payment.util.LogUtil;
import com.sioeye.youle.run.payment.util.Util;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月12日
 *
 * @fileName H5PaypalPaymentReceiver.java
 *
 * @todo H5支付paypal消费者
 */
@Service
@RefreshScope
public class H5PaypalPaymentReceiver implements ChannelAwareMessageListener {

	private static final Log logger = LogFactory.getLog(H5PaypalPaymentReceiver.class);
	
	@Value("${order.payment.fail.queue}")
	private String orderPaymentFailQueue;
	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Autowired
	private RabbitMqConfig rabbitMqConfig;
	@Autowired
	private IPaymentDao iPaymentDao;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		JSONObject result = new JSONObject();
		result = JSONObject.parseObject(new String(message.getBody()));
		try {
			Payment payment = iPaymentDao.queryPaymentByOrderId(result.getString("orderId"));
			// 设置支付时间
			payment.setPayTime(new Date());
			// 处理payment支付成功
			if (!(iPaymentDao.update(payment) > 0)) {
				throw new CustomException(EnumHandle.UPDATE_PAYMENT_FAILED);
			}
			//发送mq消息让order处理
			this.sendMqMessage(result, orderPaymentQueue);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			// 发送数据到失败的队列里
			this.sendMqMessage(result, orderPaymentFailQueue);
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, result.toJSONString());
			// 发送数据到失败的队列里
			this.sendMqMessage(result, orderPaymentFailQueue);
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		}
	}

	/**
	 * 发送mq队列消息
	 * 
	 * @param result
	 * @throws IOException
	 * @throws TimeoutException
	 *             void
	 */
	private void sendMqMessage(JSONObject result, String queue) throws IOException, TimeoutException {
		rabbitMqConfig.createMqConnection();
		// 微信app会员权益支付成功，向指定的mq消息队列中发送支付完成的消息
		Util.sendMqMessage(rabbitMqConfig.mqConnection, queue, result.toJSONString());
	}
}
