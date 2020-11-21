package com.sioeye.youle.run.order.service.receiver;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.sioeye.youle.run.order.application.IGoodsOrderAppService;
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
import com.sioeye.youle.run.order.config.EnumHighlightStatus;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.dao.IOrderDao;
import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.service.feign.IHighlightBoughtFeign;
import com.sioeye.youle.run.order.util.ConstUtil;
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
 * @Todo 订单支付成功消费类
 *
 */
@Service
@RefreshScope
public class OrderPaymentReceiver implements ChannelAwareMessageListener {

	private static final Log logger = LogFactory.getLog(OrderPaymentReceiver.class);
	@Autowired
	private IOrderDao iOrderDao;
	@Autowired
	private IHighlightBoughtFeign iHighlightBoughtFeign;
	@Autowired
	private IGoodsOrderAppService goodsOrderAppService;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Value("${order.payment.fail.queue}")
	private String orderPaymentFailQueue;
	@Value("${disney.access.key.id}")
	private String appId;
	@Value("${disney.access.key.secret}")
	private String secretKey;
	@Value("${user.merge.video.status.url}")
	private String userMergeVideoStatusUrl;
	@Value("${order.photoprint.user.url}")
	private String orderPhotoprintUserUrl;
	@Value("${order.payment.success.queue}")
	private String orderPaymentSuccessQueue;
	@Value("${order.payment.local.timezone}")
	private String orderPaymentLocalTimezone;

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		JSONObject result = new JSONObject();
		result = JSONObject.parseObject(new String(message.getBody()));
		try {
			result.put("action", "pay_completed");
			Optional.ofNullable(result.getString("orderId"))
					.orElseThrow(() -> new CustomException(EnumHandle.PARAMS_INCORRECT));
			// 查询出订单
			Order order = iOrderDao.getOrderDetail(result.getString("orderId"));
			if (order == null) {
				throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
			}
			if (EnumOrdersStatus.PAY_SUCCESS.getCode() == order.getStatus()) {
				// 订单已支付
				LogUtil.packageSuccessLog(logger, order);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				return;
			}
			// 只有集锦视频才直接更新状态
			if (EnumType.HIGHLIGHT.getCode() == order.getType()) {
				// 设置订单状态
				order.setStatus(EnumOrdersStatus.PAY_SUCCESS.getCode());
				order.setUpdateTime(new Date());
				if (!(iOrderDao.update(order) > 0)) {
					throw new CustomException(EnumHandle.UPDATE_ORDER_FAILED);
				}
			}
			logger.info("orderId:" + order.getObjectId());
			// 根据type进行不同的逻辑
			if (EnumType.CLIP.getCode() == order.getType()) {
				// 调用接口修改videobought状态为已支付，未迁移
				this.goodsOrderAppService.paidBack(order.getObjectId());
				// 调用user接口，将videobought购买标示置为购买过
				this.updateVideoMergeStatus(order.getUsersId());
			} else if (EnumType.HIGHLIGHT.getCode() == order.getType()) {
				// 调用highlight方法
				this.updateHighlightBoughtStatus(order.getObjectId());
			} else {
				// 订单支付成功回调
				this.goodsOrderAppService.paidBack(order.getObjectId());
			}
			// 成功日志
			LogUtil.packageSuccessLog(logger, order);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (CustomException e) {
			LogUtil.BusinessError(logger, e);
			// 发送数据到失败的队列里
			sendMqMessage(result, orderPaymentFailQueue);
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			LogUtil.internalError(logger, e, result.toJSONString());
			// 发送数据到失败的队列里
			sendMqMessage(result, orderPaymentFailQueue);
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		}
	}

	/**
	 * 修改用户购买视频状态
	 * 
	 * @param userId
	 * @throws NoSuchAlgorithmException
	 *             void
	 */
	private void updateVideoMergeStatus(String userId) throws NoSuchAlgorithmException {
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		params.put("videobought", ConstUtil.USER_VIDEOBOUGHT_STATUS);
		String userMergeVideoStatusStr = HttpUtil.httpsRequest(userMergeVideoStatusUrl, "POST", params.toString(),
				HttpUtil.createBaseHeader(appId, secretKey));
		JSONObject userMergeVideoStatusJson = JSONObject.parseObject(userMergeVideoStatusStr);
		ValidateUtil.validateRemoteCall("userMergeVideoStatusUrl", userMergeVideoStatusJson,
				EnumHandle.UPDATE_USER_MERGEVIDEO_STATUS_FAILURE);
	}

	/**
	 * 修改视频集锦状态
	 * 
	 * @param orderId
	 *            void
	 */
	private void updateHighlightBoughtStatus(String orderId) {
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		map.put("status", EnumHighlightStatus.MERGE_PAY_SUCCESS.getCode());
		String highlightBought = iHighlightBoughtFeign.updateHighlightBoughtStatus(map);
		JSONObject highlightBoughtJson = JSONObject.parseObject(highlightBought);
		ValidateUtil.validateRemoteCall("update_highlightbought", highlightBoughtJson,
				EnumHandle.CALL_HIGHLIGHTBOUGHT_FAILED);
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