package com.sioeye.youle.run.order.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.EnumOrdersType;
import com.sioeye.youle.run.order.config.EnumPayWay;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.dao.IOrderDao;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.DiscountTypeEnum;
import com.sioeye.youle.run.order.domain.service.CouponOrderPlaceService;
import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.service.feign.IHighlightBoughtFeign;
import com.sioeye.youle.run.order.service.feign.IPaymentFeign;
import com.sioeye.youle.run.order.service.intf.IHighLightAppletDisneyOrder;
import com.sioeye.youle.run.order.service.intf.IOrderBase;
import com.sioeye.youle.run.order.service.intf.IValidateDiscountType;
import com.sioeye.youle.run.order.util.ConstUtil;
import com.sioeye.youle.run.order.util.LogUtil;
import com.sioeye.youle.run.order.util.Util;
import com.sioeye.youle.run.order.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年1月10日
 *
 * @fileName HighLightAppletDisneyOrderService.java
 *
 * @todo 小程序视频集锦订单
 */
@Service
@Deprecated
@RefreshScope
public class HighLightAppletDisneyOrderService implements IHighLightAppletDisneyOrder {

	@Autowired
	private IOrderDao iOrderDao;
	@Autowired
	private IPaymentFeign iPaymentFeign;
	@Autowired
	private IHighlightBoughtFeign iHighlightBoughtFeign;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Autowired
	private IOrderBase iOrderBase;
	@Autowired
	private IValidateDiscountType validateDiscountType;
	@Value("${disney.access.key.id:null}")
	private String appId;
	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Value("${order.highlight.payment.description}")
	private String orderHighlightPaymentDescription;
	@Autowired
	private CouponOrderPlaceService couponOrderPlaceService;

	private static final Log logger = LogFactory.getLog(HighLightAppletDisneyOrderService.class);

	@Override
	public JSONObject placeOrder(Map<String, Object> params) throws CustomException, Exception {
		this.validateParams(params);
		JSONObject result = new JSONObject();
		// 判断用户是否已创建订单
		Order order = null;
		if (params.get("shareUserId") != null) {
			// 分享，需要重新复制一份订单数据
			order = iOrderDao.getOrderByHighlightId(params.get("shareUserId").toString(),
					params.get("highlightId").toString(), EnumType.HIGHLIGHT.getCode()); // 复制数据
																							// order
																							// =
			this.shareOrderHighlight(params.get("usersId").toString(), order, params.get("highlightId").toString());
		} else {
			// 查询出合成视频时生成的订单
			order = iOrderDao.getOrderByHighlightId(params.get("usersId").toString(),
					params.get("highlightId").toString(), EnumType.HIGHLIGHT.getCode());
			// 查询订单是否已经支付
			JSONObject orderPayStatusJson = this.validateOrderPayStatus(order);
			if (orderPayStatusJson != null) {
				// 存在已支付订单不为空，直接返回订单信息
				return orderPayStatusJson;
			}
		}
		params.put("parkName", order.getParkName());
		// 获取前端传来的优惠类型
		Object type = params.get("discountType");
		BuyerCoupon buyerCoupon = null;
		if (type != null) {
			Integer discountType = Integer.parseInt(type.toString());
			if (DiscountTypeEnum.COUPON.getCode() == discountType) {
				// 增加验证集锦的原价是否正确(验证套餐时已经验证过最新的集锦原价了)
				if (!order.getOriginalAmount().equals(Double.parseDouble(params.get("originalAmount").toString()))) {
					throw new CustomException(EnumHandle.PLACE_ORDER_ORIGINALAMOUNT_PARAM_INCORRECT);
				}
				// 验证是否套票支付
				Date orderTime = order.getOrderTime();
				buyerCoupon = validateDiscountType.validateCoupon(params, orderTime, ConstUtil.ORDER_TYPE_HIGHLIGHT);
				order.setOrderType(EnumOrdersType.COUPON.getCode());
				// 套餐下单，订单所有的实际支付价格都是0
				order.setActualAmount(BigDecimal.ZERO.doubleValue());
				if (buyerCoupon == null) {
					LogUtil.printFailedLogToJson(logger, "Order failed",
							"Order failed!because validate coupon is failed!");
					throw new CustomException(EnumHandle.ORDER_FAILED);
				}
			} else if (DiscountTypeEnum.SHARE.getCode() == discountType) {
				LogUtil.printFailedLogToJson(logger, "No share", "Don't support share active");
				throw new CustomException(EnumHandle.NOT_SUPPORT_SHARE);
			} else if (DiscountTypeEnum.PARKDISCOUNT.getCode() == discountType) {
				LogUtil.printFailedLogToJson(logger, "No PARK DISCOUNT", "Don't support park discount active");
				throw new CustomException(EnumHandle.NOT_SUPPORT_PARKDISCOUNT);
			} else {
				// 验证价格
				this.validatePrice(order, params);
			}
		}
		// 调用支付接口，创建预支付信息
		JSONObject jsonPaymentJson = prePayment(params);
		this.updateOrder(order, jsonPaymentJson); // 3:插入套票购买信息，新增步骤
		this.insertCouponOrder(buyerCoupon, params.get("highlightId").toString(), GoodsTypeEnum.HIGHLIGHT, order); // 封装返回值
		this.packageResult(order, result, jsonPaymentJson);
		return result;
	}

	/**
	 * 更新订单信息
	 * 
	 * @param order
	 * @param jsonPaymentJson
	 *            void
	 */
	private void updateOrder(Order order, JSONObject jsonPaymentJson) {
		order.setPaymentId(jsonPaymentJson.getString("objectId"));
		if (iOrderDao.updateOrder(order) < 1) {
			throw new CustomException(EnumHandle.UPDATE_ORDER_FAILED);
		}
	}

	/**
	 * 插入套票购买订单信息
	 * 
	 * @param buyerCoupon
	 * @param order
	 *            void
	 */
	private void insertCouponOrder(BuyerCoupon buyerCoupon, String goodsId, GoodsTypeEnum goodsType, Order order) {
		if (buyerCoupon != null) {
			// 转换model里的order为domain里的order
			com.sioeye.youle.run.order.domain.order.Order goodsOrder = new com.sioeye.youle.run.order.domain.order.Order(
					order.getObjectId(), buyerCoupon.getBuyer());
			couponOrderPlaceService.placeOrderCoupon(buyerCoupon, goodsId, goodsType, goodsOrder);
		}
	}

	/**
	 * 验证订单支付状态
	 * 
	 * @param order
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	private JSONObject validateOrderPayStatus(Order order) throws CustomException, Exception {
		JSONObject result = null;
		if (order == null) {
			throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
		}
		if (order.getPaymentId() == null || "".equals(order.getPaymentId())) {
			return result;
		}
		// 有数据，需要判断订单状态，返回是否可以继续创建订单
		Map<String, Object> queryOrderDetailMap = new HashMap<>();
		queryOrderDetailMap.put("orderId", order.getObjectId());
		if (order.getStatus() == EnumOrdersStatus.PAY_SUCCESS.getCode()) { // 订单状态为已支付,中断创建订单操作,直接查询订单的支付信息，不进行第三方查询
			queryOrderDetailMap.put("queryFlag", false);
			// JSONObject jsonPaymentDetail =
			// iOrderBase.getOrderDetail(queryOrderDetailMap);
			JSONObject jsonPaymentDetail = null;
			result = new JSONObject();
			packageResult(order, result, jsonPaymentDetail);
		} else {
			// 订单状态为未支付,查询订单支付详情，并且进行第三方支付查询
			queryOrderDetailMap.put("queryFlag", true);
			// JSONObject jsonPaymentDetail =
			// iOrderBase.getOrderDetail(queryOrderDetailMap);
			JSONObject jsonPaymentDetail = null;
			// 如果第三方支付查询结果为success表示已支付
			if ("SUCCESS".equals(jsonPaymentDetail.getString("tradeState"))) {
				result = new JSONObject();
				packageResult(null, result, jsonPaymentDetail);
			} else {
				// 重新生成payment支付信息
				String orderId = order.getObjectId();
				// 重新生成orderid
				order.setPaymentId(null);
				order.setObjectId(UUID.randomUUID().toString().replace("-", "").toUpperCase());
				// 重新调用远程，将orderhighlight修改
				Map<String, Object> map = new HashMap<>();
				map.put("oldOrderId", orderId);
				map.put("newOrderId", order.getObjectId());
				String updateOrderHighlightStr = iHighlightBoughtFeign.updateOrderHighlight(map);
				JSONObject updateOrderHighlightJson = JSONObject.parseObject(updateOrderHighlightStr);
				ValidateUtil.validateRemoteCall("update_orderhighlight", updateOrderHighlightJson,
						EnumHandle.CALL_ORDERHIGHLIGHT_FAILED);
				// 更新订单信息
				if (iOrderDao.updateOrderId(order, orderId) < 1) {
					throw new CustomException(EnumHandle.UPDATE_ORDER_FAILED);
				}
			}
		}
		return result;
	}

	/**
	 * 分享order,复制
	 * 
	 * @param order
	 *            void
	 */
	private Order shareOrderHighlight(String userId, Order order, String highlightId) {
		if (order == null) {
			throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
		}
		Order shareOrder = new Order();
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", shareOrder.getObjectId());
		map.put("highlightId", highlightId);
		map.put("userId", userId);
		String addHighlightStr = iHighlightBoughtFeign.addHighlight(map);
		JSONObject addHighlightJson = JSONObject.parseObject(addHighlightStr);
		ValidateUtil.validateRemoteCall("pre_payment", addHighlightJson, EnumHandle.CALL_HIGHLIGHTBOUGHT_FAILED);
		// 复制order
		shareOrder.setActivityId(order.getActivityId());
		shareOrder.setActualAmount(order.getActualAmount());
		shareOrder.setAmusementParkId(order.getAmusementParkId());
		shareOrder.setCount(order.getCount());
		shareOrder.setOrderTime(order.getOrderTime());
		shareOrder.setOrderType(order.getOrderType());
		shareOrder.setOriginalAmount(order.getOriginalAmount());
		shareOrder.setPromotionAmount(order.getPromotionAmount());
		shareOrder.setPromotionId(order.getPaymentId());
		shareOrder.setStatus(EnumOrdersStatus.UN_PAID.getCode());
		shareOrder.setType(EnumType.HIGHLIGHT.getCode());
		shareOrder.setUsersId(userId);
		if (iOrderDao.save(shareOrder) < 1) {
			throw new CustomException(EnumHandle.INSERT_ORDER_FAILED);
		}
		return shareOrder;
	}

	/**
	 * 发送mq消息
	 * 
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	private void sendMqMessage(String orderId, String prepayId, String openId) throws IOException, TimeoutException {
		rabbitMqConfig.createMqConnection();
		// 创建消息
		JSONObject message = new JSONObject();
		message.put("orderId", orderId);
		message.put("prepayId", prepayId);
		message.put("openId", openId);
		// 微信app会员权益支付成功，向指定的mq消息队列中发送支付完成的消息
		Util.sendMqMessage(rabbitMqConfig.mqConnection, orderPaymentQueue, message.toJSONString());
	}

	/**
	 * 验证价格
	 * 
	 * @param params
	 * @throws NoSuchAlgorithmException
	 *             void
	 */
	private void validatePrice(Order order, Map<String, Object> params) throws NoSuchAlgorithmException {
		if (order != null) {
			// 创建订单时验证价格
			if (!order.getOriginalAmount().equals(Double.valueOf(params.get("originalAmount").toString()))
					|| !order.getActualAmount().equals(Double.valueOf(params.get("actualAmount").toString()))) {
				throw new CustomException(EnumHandle.PRICE_INCORRECT);
			}
		}
		// 订单优惠类型:0:原价，1：普通优惠，2：套餐优惠，3：分享活动优惠
		Integer discountType = Integer.parseInt(params.get("discountType").toString());
		// 设置orderId
		params.put("orderId", order.getObjectId());
		// 订单类型
		if (discountType == EnumOrdersType.FULL.getCode()) {
			if (order.getActualAmount().equals(order.getOriginalAmount()) && order.getActualAmount() >= 0) {
				order.setOrderType(EnumOrdersType.FULL.getCode());
			} else if (order.getOriginalAmount() > order.getActualAmount()) {
				order.setOrderType(EnumOrdersType.DISCOUNT.getCode());
			} else if (order.getOriginalAmount() == 0d && order.getActualAmount() == 0d) {
				order.setOrderType(EnumOrdersType.FULL.getCode());
			} else {
				throw new CustomException(EnumHandle.PRICE_INCORRECT);
			}
		}
	}

	/**
	 * 预支付接口
	 * 
	 * @param params
	 * @return
	 */
	private JSONObject prePayment(Map<String, Object> params) {
		// 创建支付信息
		String paymentStr = iPaymentFeign.prePayment(params);
		JSONObject jsonPayment = JSONObject.parseObject(paymentStr);
		ValidateUtil.validateRemoteCall("pre_payment", jsonPayment, EnumHandle.CALL_PAYMENT_FAILED);
		JSONObject jsonPaymentResult = jsonPayment.getJSONObject("value");
		return jsonPaymentResult;
	}

	/**
	 * 验证集锦视频订单参数
	 * 
	 * @param params
	 *            void
	 */
	private void validateParams(Map<String, Object> params) {
		// 验证游乐园id
		ValidateUtil.validateEmptyAndUuid(params.get("amusementParkId"), EnumHandle.PLACE_ORDER_PARKID_PARAM_INCORRECT);
		// 验证集锦id
		ValidateUtil.validateEmptyAndString(params.get("highlightId"),
				EnumHandle.PLACE_ORDER_HIGHLIGHTID_PARAM_INCORRECT);
		// 验证支付类型
		Optional.ofNullable(params.get("payWay")).orElseGet(() -> params.put("payWay", EnumPayWay.WEIXIN.getCode()));
		// 验证openid
		ValidateUtil.validateEmptyAndString(params.get("openId"), EnumHandle.PLACE_ORDER_OPENID_PARAM_INCORRECT);
		// 验证视频详情
		StringBuffer paymentDescription = new StringBuffer(orderHighlightPaymentDescription);
		if (params.get("parkName") != null) {
			paymentDescription.append("-");
			paymentDescription.append(params.get("parkName").toString());
		}
		if (paymentDescription.length() > 128) {
			paymentDescription.subSequence(0, 120);
			paymentDescription.append("....");
		}
		Optional.ofNullable(params.get("paymentDescription"))
				.orElseGet(() -> params.put("paymentDescription", paymentDescription.toString()));
		// 设置goodsName
		Optional.ofNullable(params.get("goodsName")).orElseGet(() -> params.put("goodsName", paymentDescription));
		// 设置goodsDesc
		Optional.ofNullable(params.get("goodsDesc")).orElseGet(() -> params.put("goodsDesc", paymentDescription));
		// 设置支付ip
		Optional.ofNullable(params.get("spbillCreateIp")).orElseGet(() -> params.put("spbillCreateIp", "127.0.0.1"));
		// 验证原价
		ValidateUtil.validateEmptyAndPrice(params.get("originalAmount"),
				EnumHandle.PLACE_ORDER_ORIGINALAMOUNT_PARAM_INCORRECT);
		// 验证订单实际支付金额
		ValidateUtil.validateEmptyAndPrice(params.get("actualAmount"),
				EnumHandle.PLACE_ORDER_ACTUALAMOUNT_PARAM_INCORRECT);
		// 验证是套餐、分享活动、原价支付
		ValidateUtil.validateEmptyAndNumber(params.get("discountType"), EnumHandle.DISCOUNT_TYPE_INCORRECT);
	}

	/**
	 * 封装返回值
	 * 
	 * @param order
	 * @param result
	 *            void
	 * @throws TimeoutException
	 * @throws IOException
	 */
	private void packageResult(Order order, JSONObject result, JSONObject jsonPaymentJson)
			throws IOException, TimeoutException {
		result.put("payWay", jsonPaymentJson.getInteger("payWay"));
		result.put("sign", jsonPaymentJson.getString("sign"));
		result.put("tradeType", jsonPaymentJson.getString("tradeType"));
		result.put("prepayId", jsonPaymentJson.getString("prepayId"));
		result.put("openId", jsonPaymentJson.getString("openId"));
		result.put("updateTime", jsonPaymentJson.getDate("updateTime"));
		result.put("appId", jsonPaymentJson.getString("appId"));
		result.put("orderId", order.getObjectId());
		result.put("amusementParkId", order.getAmusementParkId());
		result.put("activityId", order.getActivityId());
		result.put("count", order.getCount());
		result.put("originalAmount", order.getOriginalAmount());
		result.put("promotionAmount", order.getPromotionAmount());
		result.put("actualAmount", order.getActualAmount());
		result.put("orderTime", order.getOrderTime());
		if (ConstUtil.DISNEY_FREE_PAYMENT.equals(jsonPaymentJson.getString("prepayId"))) {
			// prepayId的值为DISNEY_FREE_PAYMENT，表示免费订单，没有支付，直接发送支付回调mq消息
			this.sendMqMessage(order.getObjectId(), ConstUtil.DISNEY_FREE_PAYMENT, jsonPaymentJson.getString("openId"));
			// 如果订单为免单支付，那么状态直接返回还是为1支付完成
			result.put("status", EnumOrdersStatus.PAY_SUCCESS.getCode());
		} else {
			result.put("status", order.getStatus());
		}
		if (jsonPaymentJson.getJSONObject("resultData") != null) {
			// 本来是用易宝支付，已经有resultDate
			result.put("resultDate", jsonPaymentJson.getJSONObject("resultData"));
		} else {
			// 增加封装小程序调用微信的package,生成resultDate
			JSONObject resultDate = new JSONObject();
			resultDate.put("timeStamp", jsonPaymentJson.getDate("updateTime").getTime() / 1000 + "");
			resultDate.put("package", "prepay_id=" + jsonPaymentJson.getString("prepayId"));
			resultDate.put("paySign", jsonPaymentJson.getString("sign"));
			resultDate.put("appId", jsonPaymentJson.getString("appId"));
			resultDate.put("signType", "MD5");
			resultDate.put("nonceStr", jsonPaymentJson.getString("nonceStr"));
			result.put("resultDate", resultDate);
		}
	}

}
