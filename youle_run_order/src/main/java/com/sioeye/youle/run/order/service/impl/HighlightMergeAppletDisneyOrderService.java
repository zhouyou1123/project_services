package com.sioeye.youle.run.order.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumHighlightStatus;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.EnumOrdersType;
import com.sioeye.youle.run.order.config.EnumPayWay;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.dao.IOrderDao;
import com.sioeye.youle.run.order.domain.order.DiscountTypeEnum;
import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.service.intf.IHighLightMergeAppletDisneyOrder;
import com.sioeye.youle.run.order.service.intf.IValidateDiscountType;
import com.sioeye.youle.run.order.util.ConstUtil;
import com.sioeye.youle.run.order.util.HttpUtil;
import com.sioeye.youle.run.order.util.LogUtil;
import com.sioeye.youle.run.order.util.Util;
import com.sioeye.youle.run.order.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年1月22日
 *
 * @fileName HighlightMergeAppletDisneyOrderService.java
 *
 * @todo 视频合成订单
 */
@Service
@Deprecated
@RefreshScope
public class HighlightMergeAppletDisneyOrderService implements IHighLightMergeAppletDisneyOrder {

	@Autowired
	private IOrderDao iOrderDao;
	@Autowired
	private IValidateDiscountType validateDiscountType;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Value("${disney.access.key.id:null}")
	private String appId;
	@Value("${disney.access.key.secret:null}")
	private String secretKey;
	@Value("${disney.park.price.url:null}")
	private String disneyParkPriceUrl;
	@Value("${highlight.merge.queue}")
	private String highlightMergeQueue;
	@Value("${user.merge.video.status.url}")
	private String userMergeVideoStatusUrl;
	@Value("${order.highlight.payment.description}")
	private String orderHighlightPaymentDescription;

	private static final Log logger = LogFactory.getLog(HighlightMergeAppletDisneyOrderService.class);

	@Override
	public JSONObject placeOrder(Map<String, Object> params) throws CustomException, Exception {
		JSONObject result = new JSONObject();
		// 表示是视频集锦合成
		Order order = new Order();
		// 验证参数信息
		this.validateParams(order, params);
		// 获取前端传来的优惠类型
		Object type = params.get("discountType");
		if (type != null) {
			Integer discountType = Integer.parseInt(type.toString());
			//验证，如果是以分享活动合成集锦视频，则直接报错
			if (DiscountTypeEnum.SHARE.getCode() == discountType) {
				LogUtil.printFailedLogToJson(logger, "No share", "Don't support share active");
				throw new CustomException(EnumHandle.NOT_SUPPORT_SHARE);
			}else if(DiscountTypeEnum.PARKDISCOUNT.getCode() == discountType){
				LogUtil.printFailedLogToJson(logger, "No PARK DISCOUNT", "Don't support park discount active");
				throw new CustomException(EnumHandle.NOT_SUPPORT_PARKDISCOUNT);
			}
		}
		// 查询是否有该用户
		if (iOrderDao.getOrderHighlightMergingCount(params.get("usersId").toString(), EnumType.HIGHLIGHT.getCode(),
				EnumHighlightStatus.MERGING.getCode()) > 0) {
			throw new CustomException(EnumHandle.USER_MERGEING_HIGHLIGHT);
		}
		// 插入orders信息
		this.insertOrder(order);
		// 发送mq消息，让highlight 服务生成数据
		this.sendMqMessage(result, order.getObjectId(), params);
		// 设置用户正在合成集锦视频
		this.updateVideoMergeStatus(order.getUsersId());
		// 封装返回值
		this.packageResult(order, result);
		return result;
	}

	/**
	 * 验证价格
	 * 
	 * @param params
	 * @throws NoSuchAlgorithmException
	 *             void
	 */
	private void validatePrice(Order order, Map<String, Object> params) throws NoSuchAlgorithmException {
		// 验证游乐园id
		ValidateUtil.validateEmptyAndUuid(params.get("amusementParkId"), EnumHandle.PLACE_ORDER_PARKID_PARAM_INCORRECT);
		JSONObject priceParams = new JSONObject();
		priceParams.put("parkId", params.get("amusementParkId").toString());
		String parkPriceStr = HttpUtil.httpsRequest(disneyParkPriceUrl, "POST", priceParams.toString(),
				HttpUtil.createHeader(appId, secretKey));
		JSONObject parkPriceJson = JSONObject.parseObject(parkPriceStr);
		ValidateUtil.validateRemoteCall("amusementParkPriceUrl", parkPriceJson, EnumHandle.HIGHLIGHT_PRICE_ERROR);
		// 验证实际价格，增加促销后价格
		JSONArray parkPriceValueArray = parkPriceJson.getJSONArray("value");
		if (parkPriceValueArray == null || parkPriceValueArray.size() == 0) {
			throw new CustomException(EnumHandle.HIGHLIGHT_PRICE_ERROR);
		}
		JSONObject parkPriceValue = parkPriceValueArray.getJSONObject(0);
		if (parkPriceValue == null) {
			throw new CustomException(EnumHandle.HIGHLIGHT_PRICE_ERROR);
		}
		if (parkPriceValue.getString("highlightPrice") == null
				|| "".equals(parkPriceValue.getString("parkPriceValue"))) {
			parkPriceValue.put("highlightPrice", ConstUtil.PARK_HIGHLIGHT_PRICE_DEFAULT);
		}
		// 验证原价
		order.setOriginalAmount(parkPriceValue.getDouble("highlightPrice"));
		// 计算订单实际支付金额
		order.setActualAmount(parkPriceValue.getDouble("highlightPrice"));
	}

	/**
	 * 验证参数
	 * 
	 * @param order
	 * @param params
	 *            void
	 */
	@SuppressWarnings("unchecked")
	private void validateParams(Order order, Map<String, Object> params) {
		// 设置orderid
		params.put("orderId", order.getObjectId());
		// 验证usersId
		ValidateUtil.validateEmptyAndString(params.get("usersId"), EnumHandle.PLACE_ORDER_USERID_PARAM_INCORRECT);
		order.setUsersId(params.get("usersId").toString());
		// 验证游乐园id
		ValidateUtil.validateEmptyAndUuid(params.get("amusementParkId"), EnumHandle.PLACE_ORDER_PARKID_PARAM_INCORRECT);
		order.setAmusementParkId(params.get("amusementParkId").toString());
		// 验证formId
		ValidateUtil.validateEmptyAndString(params.get("formId"), EnumHandle.PLACE_ORDER_FORMID_PARAM_INCORRECT);
		// 获取购买视频片段
		ValidateUtil.validateEmptyAndList(params.get("clipIds"), EnumHandle.PLACE_ORDER_CLIP_PARAM_INCORRECT);
		List<String> clipIds = (List<String>) params.get("clipIds");
		if (clipIds.size() < ConstUtil.HIGHLIGHT_CLIPS_MIN || clipIds.size() > ConstUtil.HIGHLIGHT_CLIPS_MAX) {
			throw new CustomException(EnumHandle.PLACE_ORDER_CLIPCOUNT_PARAM_INCORRECT);
		}
		// 设置视频片段数量，默认为视频列表数组length
		Optional.ofNullable(params.get("count")).orElseGet(() -> params.put("count", 1));
		ValidateUtil.validateEmptyAndInteger(params.get("count"), EnumHandle.PLACE_ORDER_CLIPCOUNT_PARAM_INCORRECT);
		order.setCount(Integer.parseInt(params.get("count").toString()));
		// 验证openid
		ValidateUtil.validateEmptyAndString(params.get("openId"), EnumHandle.PLACE_ORDER_OPENID_PARAM_INCORRECT);
		// 验证支付类型
		Optional.ofNullable(params.get("payWay")).orElseGet(() -> params.put("payWay", EnumPayWay.WEIXIN.getCode()));
		ValidateUtil.validateEmptyAndInteger(params.get("payWay"), EnumHandle.PLACE_ORDER_PAYWAY_PARAM_INCORRECT);
		// 验证视频详情
		StringBuffer paymentDescription = new StringBuffer(orderHighlightPaymentDescription);
		if (params.get("parkName") != null) {
			order.setParkName(params.get("parkName").toString());
			paymentDescription.append("-" + params.get("parkName").toString());
		}
		if (paymentDescription.length() > 128) {
			paymentDescription.subSequence(0, 120);
			paymentDescription.append("....");
		}
		// 验证视频名称即用户昵称
		ValidateUtil.validateEmptyAndString(params.get("mergeVideoName"),
				EnumHandle.PLACE_ORDER_VIDEONAME_PARAM_INCORRECT);
		// 验证封面地址
		ValidateUtil.validateEmptyAndString(params.get("thumbnailUrl"),
				EnumHandle.PLACE_ORDER_THUMBNAILURL_PARAM_INCORRECT);
		// 验证价格
		try {
			validatePrice(order, params);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.printFailedLogToJson(logger, "validate price", "validate price is failed");
			throw new CustomException(EnumHandle.VALIDATE_PRICE_FAILED);
		}
		//验证是套餐、分享活动、原价支付
		ValidateUtil.validateEmptyAndNumber(params.get("discountType"), EnumHandle.DISCOUNT_TYPE_INCORRECT);
		// 设置order的值
		order.setStatus(EnumOrdersStatus.UN_PAID.getCode());
		order.setOrderTime(new Date());
		// 订单优惠类型:0:原价，1：普通优惠，2：套餐优惠，3：分享活动优惠
		Integer discountType = Integer.parseInt(params.get("discountType").toString());
		//初始化orderType为原价(0)或园区优惠(1),是按照原价和实际支付价格对比确定，而套餐是按照discountType来确定
		if(discountType == EnumOrdersType.FULL.getCode()) {
			if (order.getActualAmount().equals(order.getOriginalAmount()) && order.getActualAmount() >= 0) {
				order.setOrderType(EnumOrdersType.FULL.getCode());
			}
		}else if(discountType == EnumOrdersType.DISCOUNT.getCode()) {
			LogUtil.printFailedLogToJson(logger, "highlight merge no support discount", "highlight merge no support discount");
		}else if(discountType == EnumOrdersType.COUPON.getCode()){
			order.setOrderType(EnumOrdersType.COUPON.getCode());
		}else if(discountType == EnumOrdersType.SHARE.getCode()) {
			LogUtil.printFailedLogToJson(logger, "highlight merge no support share", "highlight merge no support share");
		}else if (order.getOriginalAmount() == 0d && order.getActualAmount() == 0d) {
			order.setOrderType(EnumOrdersType.FULL.getCode());
		}else {
			throw new CustomException(EnumHandle.PRICE_INCORRECT);
		}
	}

	/**
	 * 插入order
	 * 
	 * @param order
	 * @param paymentId
	 */
	private void insertOrder(Order order) {
		order.setType(EnumType.HIGHLIGHT.getCode());
		if (iOrderDao.save(order) < 1) {
			throw new CustomException(EnumHandle.INSERT_ORDER_FAILED);
		}
	}

	/**
	 * 修改用户合成集锦状态
	 * 
	 * @param userId
	 * @throws NoSuchAlgorithmException
	 *             void
	 * @throws IOException
	 */
	private void updateVideoMergeStatus(String userId) throws NoSuchAlgorithmException, IOException {
		JSONObject params = new JSONObject();
		params.put("userId", userId);
		params.put("videobought", ConstUtil.USER_VIDEOBOUGHT_STATUS);
		params.put("videomergestatus", ConstUtil.USER_MERGEVIDEO_MERGING_STATUS);
		String userMergeVideoStatusStr = HttpUtil.httpsRequest(userMergeVideoStatusUrl, "POST", params.toString(),
				HttpUtil.createBaseHeader(appId, secretKey));
		JSONObject userMergeVideoStatusJson = JSONObject.parseObject(userMergeVideoStatusStr);
		ValidateUtil.validateRemoteCall("userMergeVideoStatusUrl", userMergeVideoStatusJson,
				EnumHandle.UPDATE_USER_MERGEVIDEO_STATUS_FAILURE);
	}

	/**
	 * 发送mq消息
	 * 
	 * @param result
	 * @param orderId
	 * @param params
	 *            void
	 * @throws TimeoutException
	 * @throws IOException
	 */
	private void sendMqMessage(JSONObject result, String orderId, Map<String, Object> params)
			throws IOException, TimeoutException, CustomException, Exception {
		rabbitMqConfig.createMqConnection();
		result.put("orderId", orderId);
		result.put("formId", params.get("formId"));
		result.put("mergeVideoName", params.get("mergeVideoName"));
		result.put("amusementParkId", params.get("amusementParkId"));
		result.put("amusementParkName", params.get("parkName"));
		result.put("clipIds", params.get("clipIds"));
		result.put("gameIds", params.get("gameIds"));
		result.put("thumbnailUrl", params.get("thumbnailUrl"));
		result.put("openId", params.get("openId"));
		result.put("actualAmount", params.get("actualAmount"));
		result.put("userId", params.get("usersId"));
		Util.sendMqMessage(rabbitMqConfig.mqConnection, highlightMergeQueue, result.toJSONString());
	}

	/**
	 * 封装返回值
	 * 
	 * @param order
	 * @param result
	 *            void
	 */
	private void packageResult(Order order, JSONObject result) {
		result.put("orderId", order.getObjectId());
		result.put("amusementParkId", order.getAmusementParkId());
		result.put("activityId", order.getActivityId());
		result.put("count", order.getCount());
		result.put("originalAmount", order.getOriginalAmount());
		result.put("promotionAmount", order.getPromotionAmount());
		result.put("actualAmount", order.getActualAmount());
		result.put("orderTime", order.getOrderTime());
		result.put("status", order.getStatus());
	}
}
