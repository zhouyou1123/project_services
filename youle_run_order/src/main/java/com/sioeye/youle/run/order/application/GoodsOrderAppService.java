package com.sioeye.youle.run.order.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.config.RabbitMQConfig;
import com.sioeye.youle.run.order.context.CreateOrderRequest;
import com.sioeye.youle.run.order.context.CreateOrderResponse;
import com.sioeye.youle.run.order.context.OrderStatus;
import com.sioeye.youle.run.order.context.OrderStatusRequest;
import com.sioeye.youle.run.order.context.OrderStatusResponse;
import com.sioeye.youle.run.order.context.PersonalOrderItemResourceResponse;
import com.sioeye.youle.run.order.context.PersonalOrderResourceResponse;
import com.sioeye.youle.run.order.context.PersonalPresent;
import com.sioeye.youle.run.order.context.PersonalUserCouponResponse;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.DeviceTypeEnum;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PresentPrice;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.domain.service.OrderPlaceService;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderDo;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.interfaces.ObjectStorageService;
import com.sioeye.youle.run.order.service.impl.RedisService;
import com.sioeye.youle.run.order.util.ConstUtil;
import com.sioeye.youle.run.order.util.OrderUtils;
import com.sioeye.youle.run.order.util.Util;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class GoodsOrderAppService implements IGoodsOrderAppService {

	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Value("${order.user.create.timeout}")
	private int orderUserCreateTimeout;
	@Value("${order.present.query-goods-before-day:3}")
	private int orderPresentQueryBeforeDay;
	@Value("${log.exception.max-stack:5}")
	private int exceptionMaxStack;
	@Autowired
	private OrderPlaceService orderPlaceService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private RabbitMQConfig rabbitMqConfig;
	@Autowired
	private RedisService redisService;
	@Autowired
	private ObjectStorageService objectStorageService;
	@Autowired
	private BoughtService boughtService;
	@Autowired
	private AdminParkService parkService;

	/**
	 * 兼容老版本接口
	 * 
	 * @param params
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	@Override
	public JSONObject placeOrder(Map<String, Object> params) throws CustomException, Exception {
		CreateOrderRequest createOrderRequest = null;
		try {
			createOrderRequest = OrderUtils.convertOrderRequest(params);

		} catch (Exception ex) {
			throw new CustomException(EnumHandle.PARAMS_INCORRECT.getCode(), ex.getMessage());
		}
		return OrderUtils.convertOrderResponse(placeOrder(createOrderRequest));
	}

	@Override
	public CreateOrderResponse placeOrder(CreateOrderRequest orderRequest) {
		try {

			String createOrderKey = orderRequest.getUserId()
					+ orderRequest.getGoodsList().stream().findFirst().get().getGoodsId();
			if (redisService.getCreateOrderKey(createOrderKey) != null) {
				throw new CustomException(EnumHandle.USER_CREATING_ORDER);
			} else {
				redisService.setCreateOrderKey(createOrderKey);
			}
			CreateOrderResponse createOrderResponse = orderPlaceService.placeOrderNew(orderRequest);
			if (ConstUtil.DISNEY_FREE_PAYMENT.equals(createOrderResponse.getPrepayId())) {
				sendMqMessage(createOrderResponse.getOrderId(), ConstUtil.DISNEY_FREE_PAYMENT,
						createOrderResponse.getOpenId());
				createOrderResponse.setStatus(EnumOrdersStatus.PAY_SUCCESS.getCode());
			}
			redisService.placeOrderStatus(createOrderResponse.getOrderId());
			return createOrderResponse;
		} catch (CustomException cust) {
			throw cust;
		} catch (Exception e) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("{\"parkId\":\"" + orderRequest.getAmusementParkId());
			stringBuilder.append("\",\"openId\":\"" + orderRequest.getOpenId());
			stringBuilder.append("\",\"deviceId\":\"" + orderRequest.getDeviceRecordId());
			stringBuilder.append("\",\"discountType\":\"" + orderRequest.getDiscountType().getCode());
			stringBuilder.append("\",\"url\":\"place_order");
			stringBuilder.append("\",\"errorMessage\":\"" + e.getMessage());
			stringBuilder.append("\",\"errorStack\":\"");
			Arrays.stream(e.getStackTrace()).limit(exceptionMaxStack)
					.forEach(stack -> stringBuilder.append(stack.toString()));
			stringBuilder.append("\"}");
			log.error(stringBuilder.toString());
			throw new CustomException(DomainErrorCodeEnum.UNLOCK_REDIS_ERROR.getCode(),
					String.format(DomainErrorCodeEnum.UNLOCK_REDIS_ERROR.getMessage(), e.getMessage()));
		} finally {
		}
	}

	@Override
	public void paidBack(String orderId) {
		// 调用领域服务
		// 发送微信通知
		// 日志
		orderPlaceService.paidBack(orderId);
		redisService.payOrderStatus(orderId);

	}

	private void sendMqMessage(String orderId, String prepayId, String openId) {
		// 创建消息
		String message = "{" + "\"orderId\":\"" + orderId + "\"," + "\"prepayId\":\"" + prepayId + "\","
				+ "\"openId\":\"" + openId + "\"," + "}";
		try {
			rabbitMqConfig.createMqConnection();
			// 微信app会员权益支付成功，向指定的mq消息队列中发送支付完成的消息
			Util.sendMqMessage(rabbitMqConfig.mqConnection, orderPaymentQueue, message);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CustomException(DomainErrorCodeEnum.SEND_ORDER_PAYMENT_QUEUE_ERROR.getCode(),
					DomainErrorCodeEnum.SEND_ORDER_PAYMENT_QUEUE_ERROR.getMessage() + e.getMessage());
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw new CustomException(DomainErrorCodeEnum.SEND_ORDER_PAYMENT_QUEUE_ERROR.getCode(),
					DomainErrorCodeEnum.SEND_ORDER_PAYMENT_QUEUE_ERROR.getMessage() + e.getMessage());
		}

	}

	@Override
	public void filingBack(String orderId) {
		orderPlaceService.filingBack(orderId);
	}

	@Override
	public Order getOrder(String orderId) {
		if (!StringUtils.hasText(orderId)) {
			throw CustomException.build(DomainErrorCodeEnum.ORDER_ID_NOT_EMPTY);
		}
		return doGetOrder(orderId);
	}

	@Override
	public PersonalOrderResourceResponse getOrderResourceByUserId(String orderId, String userId) {
		if (!StringUtils.hasText(orderId)) {
			throw CustomException.build(DomainErrorCodeEnum.ORDER_ID_NOT_EMPTY);
		}
		if (!StringUtils.hasText(userId)) {
			throw CustomException.build(DomainErrorCodeEnum.USERID_PARAM_INCORRECT);
		}
		return convertToDetail(boughtService.getOrderByUserId(orderId, userId));
	}

	@Override
	@Deprecated
	public boolean checkIsBoughtGoodsByUser(String userId, String goodsId) {
		return checkIsBoughtGoodsByUser(userId, goodsId, GoodsTypeEnum.CLIP.getCode());
	}

	@Override
	public boolean checkIsBoughtGoodsByUser(String userId, String goodsId, Integer goodsType) {
		if (!StringUtils.hasText(userId)) {
			throw CustomException.build(DomainErrorCodeEnum.USERID_PARAM_INCORRECT);
		}
		if (!StringUtils.hasText(goodsId)) {
			throw CustomException.build(DomainErrorCodeEnum.GOODS_ID_NOT_EMPTY);
		}
		if (goodsType == null) {
			throw CustomException.build(DomainErrorCodeEnum.INVALID_GOODSTYPE);
		}
		return boughtService.checkIsBoughtGoods(userId, goodsId, goodsType).map(goods -> true).orElse(false);
	}

	@Override
	@Deprecated
	public Optional<Order> getPaidBoughtGoodsByUser(String userId, String goodsId) {

		return getPaidBoughtGoodsByUser(userId, goodsId, GoodsTypeEnum.CLIP.getCode());
	}

	@Override
	public Optional<Order> getOrderGoodsByUser(String userId, String goodsId, Integer goodsType) {
		if (!StringUtils.hasText(userId)) {
			throw CustomException.build(DomainErrorCodeEnum.USERID_PARAM_INCORRECT);
		}
		if (!StringUtils.hasText(goodsId)) {
			throw CustomException.build(DomainErrorCodeEnum.GOODS_ID_NOT_EMPTY);
		}
		if (goodsType == null) {
			throw CustomException.build(DomainErrorCodeEnum.INVALID_GOODSTYPE);
		}
		return boughtService.checkIsGoods(userId, goodsId, goodsType);
	}

	@Override
	public Optional<Order> getPaidBoughtGoodsByUser(String userId, String goodsId, Integer goodsType) {
		if (!StringUtils.hasText(userId)) {
			throw CustomException.build(DomainErrorCodeEnum.USERID_PARAM_INCORRECT);
		}
		if (!StringUtils.hasText(goodsId)) {
			throw CustomException.build(DomainErrorCodeEnum.GOODS_ID_NOT_EMPTY);
		}
		if (goodsType == null) {
			throw CustomException.build(DomainErrorCodeEnum.INVALID_GOODSTYPE);
		}
		return boughtService.checkIsBoughtGoods(userId, goodsId, goodsType);
	}

	private PersonalOrderResourceResponse convertToDetail(Optional<Order> oOrder) {
		// 如果是套餐，查询出套餐的信息,暂时直接查询数据库,后期会将优惠促销等统一
		oOrder.orElseThrow(() -> new CustomException(DomainErrorCodeEnum.NOT_FOUND_ORDER.getCode(),
				DomainErrorCodeEnum.NOT_FOUND_ORDER.getMessage()));
		Order order = oOrder.get();
		PersonalOrderResourceResponse orderDetailResponse = new PersonalOrderResourceResponse();
		orderDetailResponse.setActualAmount(order.getOrderAmount().getActualAmount());
		orderDetailResponse.setCurrency(order.getOrderAmount().getCurrency().toString());
		orderDetailResponse.setOpenId(order.getBuyer().getOpenId());
		orderDetailResponse.setOrderId(order.id());
		orderDetailResponse.setOrderNo(order.getOrderNo());
		orderDetailResponse.setOrderStatus(order.getOrderStatus());
		orderDetailResponse.setOriginalAmount(order.getOrderAmount().getOriginalAmount());
		orderDetailResponse.setPaymentDate(order.getPaymentDate());
		orderDetailResponse.setUserId(order.getBuyer().id());
		orderDetailResponse.setPayWay(order.getPayWay());
		orderDetailResponse.setPlaceOrderDate(order.getPlaceOrderDate());
		orderDetailResponse.setParkId(order.getPark().id());
		orderDetailResponse.setParkName(order.getPark().getParkName());
		orderDetailResponse.setOrderType(order.getOrderType());
		orderDetailResponse
				.setOrderItem(order.stream().map(item -> convertToDetail(item)).collect(Collectors.toList()));
		orderDetailResponse.setUserCoupon(convertToDetail(order));
		return orderDetailResponse;
	}

	private PersonalUserCouponResponse convertToDetail(Order order) {
		CouponGoods couponGoods = null;
		if (order.getFirstGoods().goodsType() == ConstUtil.GOODS_TYPE_COUPON) {
			couponGoods = (CouponGoods) order.getFirstGoods();
		} else {
			return null;
		}
		PersonalUserCouponResponse personalUserCouponResponse = new PersonalUserCouponResponse();
		personalUserCouponResponse.setAmusementparkid(couponGoods.getPark().id());
		personalUserCouponResponse.setEnddate(couponGoods.getEndDate());
		personalUserCouponResponse.setStartdate(couponGoods.getStartDate());
		return personalUserCouponResponse;
	}

	private PersonalOrderItemResourceResponse convertToDetail(OrderItem orderItem) {

		PersonalOrderItemResourceResponse orderItemDetailResponse = new PersonalOrderItemResourceResponse();
		orderItemDetailResponse.setActualAmount(orderItem.getActualAmount());
		orderItemDetailResponse.setCount(orderItem.getCount());
		orderItemDetailResponse.setCreateDate(orderItem.getCreateDate());
		orderItemDetailResponse.setDownloadUrl(orderItem.getDownloadUrl());
		orderItemDetailResponse.setFilingStatus(orderItem.getFilingStatus());
		orderItemDetailResponse.setActualAmount(orderItem.getActualAmount());
		orderItemDetailResponse.setGoodsId(orderItem.getGoods().id());
		orderItemDetailResponse.setOrderItemId(orderItem.id());
		orderItemDetailResponse.setPrice(orderItem.getPrice());

		orderItemDetailResponse.setParkId(orderItem.getGoods().getPark().id());
		orderItemDetailResponse.setParkName(orderItem.getGoods().getPark().getParkName());
		if (orderItem.getGoods().getGame() != null) {
			orderItemDetailResponse.setGameId(orderItem.getGoods().getGame().id());
			orderItemDetailResponse.setGameName(orderItem.getGoods().getGame().getGameName());
		}
		orderItemDetailResponse.setType(orderItem.getGoods().getType());
		// 返回地址
		orderItemDetailResponse.setDownloadUrl(
				objectStorageService.getSignUrl(orderItem.getFilingStatus().getCode(), orderItem.getDownloadUrl()));
		orderItemDetailResponse.setPreviewUrl(
				objectStorageService.convertCDNUrl(orderItem.getFilingStatus().getCode(), orderItem.getPreviewUrl()));
		orderItemDetailResponse.setThumbnailUrl(
				objectStorageService.convertCDNUrl(orderItem.getFilingStatus().getCode(), orderItem.getThumbnailUrl()));
		return orderItemDetailResponse;
	}

	private Order doGetOrder(String orderId) {
		return boughtService.getOrder(orderId);
	}

	@Override
	public OrderStatusResponse getOrderStatus(Map<String, Object> params) {
		OrderStatusRequest orderStatusRequest = OrderUtils.converOrderStatus(params);
		OrderStatusResponse result = new OrderStatusResponse();
		List<OrderStatus> orderIdList = new ArrayList<OrderStatus>();
		if (orderStatusRequest.getDeviceType() == DeviceTypeEnum.MINIPROGRAM.getCode()) {
			for (String orderId : orderStatusRequest.getOrderIdList()) {
				// 直接查询数据库状态
				OrderDo orderDo = orderRepository.getOrderByUserId(orderId, params.get("userId").toString());
				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setOrderId(orderId);
				orderStatus.setOrderStatus(
						orderDo != null ? orderDo.getStatus().intValue() : EnumOrdersStatus.UN_ORDER.getCode());
				orderIdList.add(orderStatus);
				result.setOrderIdList(orderIdList);
			}
		} else {
			for (String orderId : orderStatusRequest.getOrderIdList()) {
				// 查询redis的状态
				String status = redisService.getOrderStatus(orderId);
				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setOrderId(orderId);
				orderStatus.setOrderStatus((StringUtils.hasText(status) ? EnumOrdersStatus.valueOfString(status)
						: EnumOrdersStatus.UN_ORDER).getCode());
				orderIdList.add(orderStatus);
				result.setOrderIdList(orderIdList);
			}
		}
		return result;
	}

	public List<PersonalPresent> getPersonalPresentRight(String userId, String parkId) {
		if (!StringUtils.hasText(userId)) {
			throw CustomException.build(DomainErrorCodeEnum.USERID_PARAM_INCORRECT);
		}
		if (!StringUtils.hasText(parkId)) {
			throw CustomException.build(DomainErrorCodeEnum.PLACE_ORDER_PARKID_PARAM_INCORRECT);
		}
		ParkGoodsPrice parkGoodsPrice = parkService.getParkGoodsPrice(parkId, null);
		return parkGoodsPrice.getPriceList().stream()
				.filter(goods -> goods.getPresentPrice() != null).map(goods -> this.getPersonalPresentRight(userId,
						parkId, goods.getGoodsPrice().getType(), parkGoodsPrice.getTimeZone(), goods.getPresentPrice()))
				.collect(Collectors.toList());

	}

	private PersonalPresent getPersonalPresentRight(String userId, String parkId, Integer goodsType, String timeZone,
			PresentPrice presentPrice) {
		PersonalPresent personalPresent = new PersonalPresent();
		personalPresent.setGoodsType(goodsType);
		int orderCountByPresent = orderRepository.getOrderCountByPresent(userId, parkId, goodsType,
				presentPrice.getStartTime(), presentPrice.getEndTime());
		if (presentPrice.validateUsePromotion(orderCountByPresent, timeZone)) {
			personalPresent.setUsePresent(true);
			Date startDate = new Date(
					presentPrice.getStartTime().getTime() - orderPresentQueryBeforeDay * 24 * 60 * 60 * 1000);
			personalPresent.setGoodsIds(orderRepository.getGoodsIdsByUserPark(userId, parkId, goodsType, startDate,
					presentPrice.getEndTime()));
		} else {
			personalPresent.setUsePresent(false);
		}
		return personalPresent;

	}
}
