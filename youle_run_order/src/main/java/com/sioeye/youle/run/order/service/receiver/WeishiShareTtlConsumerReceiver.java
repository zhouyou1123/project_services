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
import com.sioeye.youle.run.order.config.EnumWeishiSate;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.gateways.repository.dao.OrderItemDao;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemDo;
import com.sioeye.youle.run.order.util.HttpUtil;
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
public class WeishiShareTtlConsumerReceiver implements ChannelAwareMessageListener {

	private static final Log logger = LogFactory.getLog(WeishiShareTtlConsumerReceiver.class);
	@Autowired
	private OrderItemDao orderItemDao;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Value("${weishi.share.ttl.queue}")
	private String weishiShareTtlQueue;
	@Value("${disney.access.key.id}")
	private String appId;
	@Value("${disney.access.key.secret}")
	private String secretKey;
	@Value("${weishi.share.ttl.fail.queue}")
	private String weishiShareTtlFailQueue;
	@Value("${weishi.share.ttl.expire.time}")
	private long weishiShareTtlExpireTime;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		JSONObject result = new JSONObject();
		result = JSONObject.parseObject(new String(message.getBody()));
		logger.info("weishi_share_ttl_queue:" + result);
		try {
			// 获取orderId和seqId
			String orderId = result.getString("orderId");
			String seqId = result.getString("seqId");
			String weishiCheckStateUrl = result.getString("shareLinkApi");
			ValidateUtil.validateEmptyAndString(orderId, EnumHandle.PARAMS_INCORRECT);
			ValidateUtil.validateEmptyAndString(seqId, EnumHandle.PARAMS_INCORRECT);
			ValidateUtil.validateEmptyAndString(weishiCheckStateUrl, EnumHandle.PARAMS_INCORRECT);
			// 查询结果
			OrderItemDo orderItemDo = orderItemDao.selectByPrimaryKey(orderId);
			if (orderItemDo == null) {
				throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
			}
			// 判断订单时间是否超过指定生成微视的极限时间
			if (System.currentTimeMillis() - weishiShareTtlExpireTime > orderItemDo.getCreatetime().getTime()) {
				// 发送到失败队列里面
				this.sendMqMessage(result, weishiShareTtlFailQueue);
				return;
			}
			// 调用接口，获取是否生成微视观看地址
			JSONObject params = new JSONObject();
			params.put("seq_id", seqId);
			// 检查微视分享文件状态接口地址
			String weishiCheckFileStateStr = HttpUtil.httpsRequest(weishiCheckStateUrl, "POST", params.toString(),
					HttpUtil.createHeader(appId, secretKey));
			JSONObject weishiCheckFileStateJson = JSONObject.parseObject(weishiCheckFileStateStr);
			ValidateUtil.validateRemoteCall(weishiCheckStateUrl, weishiCheckFileStateJson,
					EnumHandle.CALL_CORE_CHECK_STATE_FAILURE);
			JSONObject weishiCheckFileStateValueJson = weishiCheckFileStateJson.getJSONObject("value");
			// 检查微视视频处理状态
			Integer state = weishiCheckFileStateValueJson.getInteger("status");
			if (state == EnumWeishiSate.PUBLISH_SUCCESS.getCode()) {
				orderItemDo.setShareurl(weishiCheckFileStateValueJson.getString("url"));
				if (!(orderItemDao.updateByPrimaryKey(orderItemDo) > 0)) {
					throw new CustomException(EnumHandle.UPDATE_ORDER_FAILED);
				}
				// 成功日志
				LogUtil.packageSuccessLog(logger, null);
			} else if (state == EnumWeishiSate.PUBLISHING.getCode()
					|| state == EnumWeishiSate.DOWNLOADING_COVER.getCode()
					|| state == EnumWeishiSate.UPLOADING_COVER.getCode()
					|| state == EnumWeishiSate.DOWNLOADING_VIDEO.getCode()
					|| state == EnumWeishiSate.UPLOADING_VIDEO.getCode()) {
				// 微视处理中,继续放入ttl的队列
				this.sendMqMessage(result, weishiShareTtlQueue);
			} else {
				// 发送到失败队列里面
				this.sendMqMessage(result, weishiShareTtlFailQueue);
			}
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			// 发送数据到失败的队列里
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, result.toJSONString());
			// 发送数据到失败的队列里
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} finally {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
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
