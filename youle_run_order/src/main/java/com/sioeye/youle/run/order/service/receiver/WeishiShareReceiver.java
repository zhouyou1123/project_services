package com.sioeye.youle.run.order.service.receiver;

import java.io.IOException;
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
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.dao.IOrderDao;
import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.util.LogUtil;
import com.sioeye.youle.run.order.util.Util;
import com.sioeye.youle.run.order.util.ValidateUtil;

/**
 * 
 * @author zhouyou email jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月7日
 * 
 * @ClassName VideoPaymentReceiver.java
 * 
 * @Version v2.0.1
 *
 * @Todo 微视分享地址查询
 *
 */
@Service
@RefreshScope
public class WeishiShareReceiver implements ChannelAwareMessageListener {

	private static final Log logger = LogFactory.getLog(WeishiShareReceiver.class);
	@Autowired
	private IOrderDao iOrderDao;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Value("${weishi.share.ttl.queue}")
	private String weishiShareTtlQueue;
	@Value("${disney.access.key.id}")
	private String appId;
	@Value("${disney.access.key.secret}")
	private String secretKey;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		JSONObject result = new JSONObject();
		result = JSONObject.parseObject(new String(message.getBody()));
		try {
			logger.info("upload_wesee_message_queue:" + result);
			JSONObject resultValue = result.getJSONObject("value");
			// 获取orderId和seqId
			String orderId = resultValue.getString("orderid");
			String seqId = resultValue.getString("seq_id");
			String shareLinkApi = resultValue.getString("shareLinkApi");
			ValidateUtil.validateEmptyAndString(orderId, EnumHandle.PARAMS_INCORRECT);
			ValidateUtil.validateEmptyAndString(seqId, EnumHandle.PARAMS_INCORRECT);
			ValidateUtil.validateEmptyAndString(shareLinkApi, EnumHandle.PARAMS_INCORRECT);
			// 查询结果
			Order order = iOrderDao.getOrderDetail(orderId);
			if (order == null) {
				throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
			}
			JSONObject ttlMap = new JSONObject();
			ttlMap.put("orderId", orderId);
			ttlMap.put("seqId", seqId);
			ttlMap.put("shareLinkApi", shareLinkApi);
			this.sendMqMessage(ttlMap, weishiShareTtlQueue);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			// 发送数据到失败的队列里
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, result.toJSONString());
			// 发送数据到失败的队列里
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
