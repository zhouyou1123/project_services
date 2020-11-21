package com.sioeye.youle.run.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.application.IGoodsOrderAppService;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumPayWay;
import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.context.BoughtOrderResponse;
import com.sioeye.youle.run.order.context.BoughtOrderResultResponse;
import com.sioeye.youle.run.order.context.DisplayRequest;
import com.sioeye.youle.run.order.context.DisplayResponse;
import com.sioeye.youle.run.order.context.GoodsResponse;
import com.sioeye.youle.run.order.context.OrderResponse;
import com.sioeye.youle.run.order.context.PriceResponse;
import com.sioeye.youle.run.order.dao.IOrderDao;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.goods.ResourceGoods;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;
import com.sioeye.youle.run.order.domain.price.CouponPrice;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.price.GoodsPriceConfiguration;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionPrice;
import com.sioeye.youle.run.order.domain.price.ShareActivityPrice;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.domain.service.DisplayOrderService;
import com.sioeye.youle.run.order.gateways.client.S3ObjectStorageServiceClient;
import com.sioeye.youle.run.order.gateways.repository.dataobject.BoughtOrderDo;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.interfaces.RunCoreService;
import com.sioeye.youle.run.order.service.feign.IHighlightBoughtFeign;
import com.sioeye.youle.run.order.service.intf.IHighLightAppletDisneyOrder;
import com.sioeye.youle.run.order.service.intf.IHighLightMergeAppletDisneyOrder;
import com.sioeye.youle.run.order.service.intf.IOrder;
import com.sioeye.youle.run.order.service.intf.IOrderBase;
import com.sioeye.youle.run.order.util.ConstUtil;
import com.sioeye.youle.run.order.util.OrderUtils;
import com.sioeye.youle.run.order.util.Util;
import com.sioeye.youle.run.order.util.ValidateUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName OrderImpl.java
 *
 * @todo 订单基础实现类
 */
@RefreshScope
@Service
public class OrderBaseService implements IOrderBase {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private IHighLightAppletDisneyOrder iHighLightAppletDisneyOrder;
	@Autowired
	private IHighLightMergeAppletDisneyOrder iHighLightMergeAppletDisneyOrder;
	@Autowired
	private IOrderDao iOrderDao;
	@Autowired
	private IHighlightBoughtFeign iHighlightBoughtFeign;
	@Autowired
	private AdminParkService adminParkService;
	@Autowired
	private IGoodsOrderAppService iGoodsOrderAppService;
	@Autowired
	private BoughtService boughtService;
	@Autowired
	private RunCoreService runCoreService;
	@Autowired
	private S3ObjectStorageServiceClient s3ObjectStorageServiceClient;
	@Autowired
	private DisplayOrderService displayOrderService;
	@Value("${default.sioeye.payway}")
	private Integer defaultSioeyePayWay;
	@Value("${default.sioeye.enable.payway}")
	private Boolean defaultSioeyeEnablePayWay;
	@Value("${order.promotion.time}")
	private int orderPromotionTime;
	@Value("${order.promotion.photo.time}")
	private int orderPromotionPhotoTime;
	@Value("${disney.access.key.id}")
	private String appId;
	@Value("${disney.access.key.secret}")
	private String secretKey;
	@Value("${aws.s3.aeon.bucket}")
	private String disneyS3Bucket;
	@Value("${aws.s3.tmp.bucket}")
	private String disneyS3TmpBucket;
	@Value("${aws.cloud-front.tmp}")
	private String disneyCloudfrontTmpAddr;
	@Value("${aws.cloud-front.aeon}")
	private String disneyCloudfrontAddr;

	@Override
	public JSONObject placeOrder(Map<String, Object> params) throws CustomException, Exception {
		// 交易类型，设置默认为小程序游乐园
		// EnumTradeType.validateTradeType(params.get("tradeType"));
		// IOrder iOrderBase =
		// getOrderService(params.get("tradeType").toString());
		params.put("payWay",
				EnumPayWay.validatePayWay(params.get("payWay"), defaultSioeyeEnablePayWay, defaultSioeyePayWay));
		// 调用预下单方法
		return iGoodsOrderAppService.placeOrder(params);
	}

	@Override
	public JSONObject getOrderHighlightUrl(String tokenUserId, Map<String, Object> params)
			throws CustomException, Exception {
		// 验证参数
		// ValidateUtil.validateEmptyAndString(params.get("orderId"),
		// EnumHandle.NOT_FOUND_ORDER);
		// Order order =
		// iOrderDao.getOrderDetail(params.get("orderId").toString());
		// if (order == null) {
		// throw new CustomException(EnumHandle.NOT_FOUND_ORDER);
		// }
		// // 判断订单状态
		// if (EnumOrdersStatus.PAY_SUCCESS.getCode() != order.getStatus()) {
		// Map<String, Object> map = new HashMap<>();
		// map.put("orderId", order.getObjectId());
		// ValidateUtil.validateEmptyAndString(order.getPaymentId(),
		// EnumHandle.ORDER_HASNOT_PAYMENT);
		// map.put("paymentId", order.getPaymentId());
		// map.put("orderStatus", order.getStatus());
		// map.put("orderId", order.getObjectId());
		// map.put("queryFlag", true);
		// JSONObject jsonPaymentDetailJson = this.getPaymentDetail(order, map);
		// if (!"SUCCESS".equals(jsonPaymentDetailJson.getString("tradeState")))
		// {
		// // 如果第三方支付查询结果未支付，返回错误
		// throw new CustomException(EnumHandle.ORDER_UNPAY);
		// }
		// }
		// // 订单已支付
		// String highlightBoughtStr =
		// iHighlightBoughtFeign.getOrderHighlightUrl(params);
		// JSONObject highlightBoughtJson =
		// JSONObject.parseObject(highlightBoughtStr);
		// ValidateUtil.validateRemoteCall("get_order_highlighturl",
		// highlightBoughtJson,
		// EnumHandle.CALL_HIGHLIGHTBOUGHT_FAILED);
		// JSONObject highlightBoughtValue =
		// highlightBoughtJson.getJSONObject("value");
		// if (highlightBoughtValue == null) {
		// throw new CustomException(EnumHandle.CALL_HIGHLIGHTBOUGHT_FAILED);
		// }
		// return highlightBoughtValue;
		return null;
	}

	/**
	 * 查询
	 * 
	 * @param order
	 * @param map
	 * @return JSONObject
	 */
	private JSONObject getPaymentDetail(Map<String, Object> map) {
		JSONObject result = new JSONObject();
		// String payment = iPaymentFeign.getPaymentDetail(map);
		// JSONObject paymentJsonObject = JSONObject.parseObject(payment);
		// ValidateUtil.validateRemoteCall("get_payment_detail",
		// paymentJsonObject, EnumHandle.CALL_PAYMENT_FAILED);
		// JSONObject paymentValueJsonObject =
		// paymentJsonObject.getJSONObject("value");
		// // 设置返回值
		// result.put("tradeState",
		// paymentValueJsonObject.getString("tradeState"));
		// result.put("tradeStateDesc",
		// paymentValueJsonObject.getString("tradeStateDesc"));
		// result.put("openId", paymentValueJsonObject.getString("openId"));
		// result.put("sign", paymentValueJsonObject.getString("sign"));
		// result.put("payWay", paymentValueJsonObject.getString("payWay"));
		// result.put("tradeType",
		// paymentValueJsonObject.getString("tradeType"));
		// result.put("appId", paymentValueJsonObject.getString("appId"));
		// result.put("prepayId", paymentValueJsonObject.getString("prepayId"));
		// result.put("orderId", map.get("orderId").toString());
		// result.put("usersId", order.getUsersId());
		// result.put("amusementParkId", order.getAmusementParkId());
		// result.put("activityId", order.getActivityId());
		// result.put("count", order.getCount());
		// result.put("originalAmount", order.getOriginalAmount());
		// result.put("promoteAmount", order.getPromotionAmount());
		// result.put("actualAmount", order.getActualAmount());
		// result.put("status", order.getStatus());
		// result.put("orderTime", order.getOrderTime());
		// result.put("updateTime", order.getUpdateTime());
		return result;
	}

	/**
	 * 根据交易类型获取IOrder
	 * 
	 * @param tradeType
	 * @return
	 */
	private IOrder getOrderService(String tradeType) {
		if (EnumTradeType.HIGHLIGHT_APPLET_DISNEY.toString().equals(tradeType)) {
			// 小程序视频集锦下单
			return iHighLightAppletDisneyOrder;
		} else if (EnumTradeType.HIGHLIGHT_MERGE_APPLET_DISNEY.toString().equals(tradeType)) {
			// 小程序视频合成
			return iHighLightMergeAppletDisneyOrder;
		} else if (EnumTradeType.APPLET_DISNEY.toString().equals(tradeType)
				|| EnumTradeType.COUPON.toString().equals(tradeType)
				|| EnumTradeType.PRINT1ADD1.toString().equals(tradeType)
				|| EnumTradeType.APPLET_PHOTO.toString().equals(tradeType)
				|| EnumTradeType.H5_TERMINAL_PHOTO.toString().equals(tradeType)) {
			return iGoodsOrderAppService;
		}
		throw new CustomException(EnumHandle.TRADE_TYPE_ERROR);
	}

	/**
	 * 
	 * @Description:桌面终端照片打印预览图片签名
	 * @Author GuoGongwei
	 * @Time: 2019年9月12日下午2:19:10
	 * @param waitsignedUrl
	 * @return
	 * @throws Exception
	 */
	private String signUrl(String waitsignedUrl, int type) throws Exception {
		// 移除http://头
		if (waitsignedUrl.contains("http://") || waitsignedUrl.contains("https://")) {
			waitsignedUrl = waitsignedUrl.replace("http://", "");
			waitsignedUrl = waitsignedUrl.replace("https://", "");
			waitsignedUrl = waitsignedUrl.substring(waitsignedUrl.indexOf("/") + 1,
					waitsignedUrl.indexOf("?") > 0 ? waitsignedUrl.indexOf("?") : waitsignedUrl.length());
		}
		// 移除s3://头
		if (waitsignedUrl.contains("s3://")) {
			waitsignedUrl = waitsignedUrl.replace("s3://", "");
			waitsignedUrl = waitsignedUrl.substring(waitsignedUrl.indexOf("/") + 1,
					waitsignedUrl.indexOf("?") > 0 ? waitsignedUrl.indexOf("?") : waitsignedUrl.length());
		}
		// s3访问加密url
		if (ConstUtil.S3_ADDR_TMP == type) {
			return s3ObjectStorageServiceClient.getDownloadUrl(disneyS3TmpBucket, waitsignedUrl);
		} else if (ConstUtil.S3_ADDR_AEON == type) {
			return s3ObjectStorageServiceClient.getDownloadUrl(disneyS3Bucket, waitsignedUrl);
		} else {
			throw new CustomException(EnumHandle.PARAMS_INCORRECT);
		}
	}

	/**
	 * 替换s3地址为cdn地址
	 * 
	 * @param s3Addr
	 * @param type
	 * @return String
	 */
	private String s3AddrToCdn(String s3Addr, int type) {
		if (s3Addr == null || "".equals(s3Addr)) {
			return "";
		}
		StringBuffer addr = new StringBuffer();
		if (s3Addr.contains("http://") || s3Addr.contains("https://") || s3Addr.contains("s3://")
				|| s3Addr.contains("s3://")) {
			s3Addr = s3Addr.replace("http://", "");
			s3Addr = s3Addr.replace("https://", "");
			s3Addr = s3Addr.replace("s3://", "");
			s3Addr = s3Addr.replace("s3://", "");
			if (s3Addr.lastIndexOf("/") > 0) {
				addr = new StringBuffer(s3Addr);
				addr.delete(0, s3Addr.indexOf("/"));
				if (ConstUtil.S3_ADDR_TMP == type) {
					addr.insert(0, disneyCloudfrontTmpAddr);
				} else if (ConstUtil.S3_ADDR_AEON == type) {
					addr.insert(0, disneyCloudfrontAddr);
				} else {
					throw new CustomException(EnumHandle.PARAMS_INCORRECT);
				}
			}
		} else {
			addr = new StringBuffer(s3Addr);
			if (ConstUtil.S3_ADDR_TMP == type) {
				addr.insert(0, disneyCloudfrontTmpAddr + "/");
			} else if (ConstUtil.S3_ADDR_AEON == type) {
				addr.insert(0, disneyCloudfrontAddr + "/");
			} else {
				throw new CustomException(EnumHandle.PARAMS_INCORRECT);
			}
		}
		return addr.toString();
	}

	@Override
	public DisplayResponse display(Map<String, Object> map) throws CustomException, Exception {
		DisplayResponse result = new DisplayResponse();
		// 转换参数
		DisplayRequest request = OrderUtils.convertDisplay(map);
		// goodsType和ValidateDuplicate
		displayOrderService.setValidateDuplicateAndGoodsType(request);
		// 获取已购订单
		Optional<Order> orderExsit = displayOrderService.getOrderGoodsByUser(request);
		// 返回订单信息
		this.getOrderResponse(orderExsit, request.getGoodsType(), result::setOrder);
		// 返回商品信息
		GoodsResponse goods = this.getGoodsResponse(request, orderExsit, result::setGoods);
		// 返回价格信息
		this.parkPrice(adminParkService::getParkGoodsPrice, request.getGoodsType(), request.getUserId(),
				request.getParkId(), goods.getGameId(), result::setPrice);
		return result;
	}

	public void getOrderResponse(Optional<Order> orderExsit, Integer goodsType, Consumer<OrderResponse> consumer) {
		OrderResponse orderResponse = new OrderResponse();
		if (orderExsit != null && orderExsit.isPresent()
				&& OrderStatusEnum.PAID.compareTo(orderExsit.get().getOrderStatus()) == 0) {
			// 已支付
			Order order = orderExsit.get();
			OrderItem orderItem = order.stream().filter(oi -> oi.getGoods().getType() == goodsType).findAny()
					.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.INVALID_ORDER_ITEM));
			orderResponse.setOrderId(orderExsit.get().id());
			orderResponse.setOrderTime(orderExsit.get().getPlaceOrderDate());
			orderResponse.setPaymentTime(orderExsit.get().getPaymentDate());
			orderResponse.setOrderType(orderExsit.get().getOrderType());
			orderResponse.setStatus(OrderStatusEnum.PAID.getCode());
			orderResponse.setOriginalAmount(orderItem.getOriginalAmount());
			orderResponse.setActualAmount(orderItem.getActualAmount());
			// 如果是套餐,那么需要特殊处理订单的状态,根据订单套餐的开始时间和结束时间与当前时间比较,返回套餐是否可用来觉得订单状态
			if (goodsType == ConstUtil.GOODS_TYPE_COUPON) {
				CouponGoods couponGoods = (CouponGoods) orderItem.getGoods();
				orderResponse.setStatus(couponGoods.returnCouponOverdue() ? OrderStatusEnum.PAID.getCode()
						: OrderStatusEnum.NOT_PAY.getCode());
			}
		} else {
			// 未购买
			orderResponse.setStatus(OrderStatusEnum.NOT_PAY.getCode());
		}
		consumer.accept(orderResponse);
	}

	private GoodsResponse getGoodsResponse(DisplayRequest request, Optional<Order> orderExsit,
			Consumer<GoodsResponse> consumer) throws Exception {
		GoodsResponse goods = null;
		if (request.getGoodsType() == ConstUtil.GOODS_TYPE_COUPON) {
			// 如果是套餐，那么直接返回套餐的配置价格
			goods = new GoodsResponse(
					request.getResourceCategory() != null ? request.getResourceCategory().getCode() : null,
					request.getResourceType(), request.getGoodsId(), request.getGoodsType(), request.getParkId(), null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		} else if (request.getNeedValidateDuplicate() && orderExsit != null && orderExsit.isPresent()
				&& orderExsit.get().getOrderStatus().compareTo(OrderStatusEnum.PAID) == 0) {
			// 已购买
			OrderItem ot = orderExsit.get().stream().filter(oi -> request.getGoodsType() == oi.getGoods().getType())
					.findFirst().orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.INVALID_ORDER_ITEM));
			ResourceGoods resourceGoods = (ResourceGoods) ot.getGoods();
			goods = new GoodsResponse(resourceGoods.getResourceCategory().getCode(), resourceGoods.getResourceType(),
					request.getGoodsId(), request.getGoodsType(), resourceGoods.getPark().id(),
					resourceGoods.getPark().getParkName(), resourceGoods.getGame().id(),
					resourceGoods.getGame().getGameName(),
					resourceGoods.getSeat() != null ? resourceGoods.getSeat().id() : null,
					resourceGoods.getSeat() != null ? resourceGoods.getSeat().getSequenceNo() : null,
					resourceGoods.getSeat() != null ? resourceGoods.getSeat().getMark() : null,
					this.s3AddrToCdn(ot.getThumbnailUrl(), ConstUtil.S3_ADDR_AEON),
					//这里本来是预览地址字段，为了小程序兼容，将已购的下载地址存入到预览地址
					s3ObjectStorageServiceClient.getSignUrl(2, ot.getDownloadUrl()),
					s3ObjectStorageServiceClient.getSignUrl(2, ot.getDownloadUrl()), resourceGoods.getClipWidth(),
					resourceGoods.getClipHeight(), resourceGoods.getClipSize(),
					resourceGoods.getClipDuration() != null ? resourceGoods.getClipDuration() : null,
					resourceGoods.getCreateDate(),
					resourceGoods.getClip() != null ? resourceGoods.getClip().getStartTime() : null,
					resourceGoods.getActivity() != null ? resourceGoods.getActivity().id() : null);
		} else {
			// 未购买
			Resource resource = runCoreService.getResource(request.getGoodsId(), request.getResourceType(),
					request.getResourceCategory());
			goods = new GoodsResponse(resource.getResourceCategory().getCode(), resource.getResourceType(),
					request.getGoodsId(), request.getGoodsType(), resource.getParkId(), resource.getParkName(),
					resource.getGameId(), resource.getGameName(), resource.getSeatId(), resource.getSeatName(),
					resource.getSeatSequenceNo(), this.s3AddrToCdn(resource.getThumbnailUrl(), ConstUtil.S3_ADDR_TMP),
					this.s3AddrToCdn(resource.getPreviewUrl(), ConstUtil.S3_ADDR_TMP), null, resource.getWidth(),
					resource.getHeight(), resource.getSize(),
					resource.getDuration() != null ? Math.round(resource.getDuration()) : null,
					resource.getCreateTime(), resource.getShootTime(), resource.getActivityId());
		}
		// 判断parkId是否是传入的amusementParkId
		if (!request.getParkId().equals(goods.getParkId())) {
			throw new CustomException(EnumHandle.PARAMS_NOT_MATCH);
		}
		consumer.accept(goods);
		return goods;
	}

	private void parkPrice(BiFunction<String, String, ParkGoodsPrice> parkGoodsPriceFunction, Integer goodsType,
			String userId, String parkId, String gameId, Consumer<PriceResponse> consumer) {
		// 获取游乐园价格
		ParkGoodsPrice parkGoodsPrice = parkGoodsPriceFunction.apply(parkId, gameId);
		Optional.ofNullable(parkGoodsPrice).orElseThrow(() -> new CustomException(EnumHandle.GET_PARK_PRICE_ERROR));
		PriceResponse priceResponse = new PriceResponse();
		priceResponse.setCurrency(parkGoodsPrice.getCurrency());
		priceResponse.setEnabled(parkGoodsPrice.getEnabled());
		priceResponse.setStopped(parkGoodsPrice.getStopped());
		priceResponse.setTimeZone(parkGoodsPrice.getTimeZone());
		// 游乐园园区配置价格信息集合（包含多组商品信息、园区优惠、第三方分享和套餐）
		List<Object> priceList = new LinkedList<Object>();
		// 商品价格配置json
		Optional<GoodsPriceConfiguration> currentPriceConfig = parkGoodsPrice.getGoodsPriceConfig(goodsType);
		currentPriceConfig.ifPresent(price -> {
			JSONObject goodsPriceConfigJson = new JSONObject();
			// 组装游乐园后台配置的商品价格信息
			goodsPriceConfigJson.put("goodsPrice", price.getGoodsPrice());
			// 组装游乐园后台配置的普通园区优惠
			this.getPromotionPriceConfig(goodsPriceConfigJson, userId, parkId, price, parkGoodsPrice.getTimeZone());
			// 组装游乐园后台配置的第三方分享信息
			Collection<ShareActivityPrice> shareList = price.getSharePrice();
			goodsPriceConfigJson.put("sharePrice", shareList);
			if (shareList != null && shareList.size() > 0) {
				for (ShareActivityPrice shareActivityPrice : shareList) {
					shareActivityPrice.setUploadApi(null);
				}
			}
			// 组装游乐园后台配置的套餐价格
			this.getCouponPriceConfig(goodsPriceConfigJson, goodsType, userId, parkId, parkGoodsPrice,
					parkGoodsPrice.getTimeZone());
			priceList.add(goodsPriceConfigJson);
		});
		priceResponse.setPriceList(priceList);
		consumer.accept(priceResponse);
	}

	/**
	 * 
	 * @Description:整合游乐园后台配置的普通园区优惠
	 * @Time: 2019年10月11日下午2:36:44
	 * @param goodsPrice
	 * @param map
	 * @param goodsPriceConfig
	 * @return
	 */
	private void getPromotionPriceConfig(JSONObject goodsPriceConfigJson, String userId, String parkId,
			GoodsPriceConfiguration goodsPriceConfig, String timeZone) {
		PromotionPrice promotionPrice = goodsPriceConfig.getPromotionPrice();
		GoodsPrice goodsPrice = goodsPriceConfig.getGoodsPrice();
		// 普通园区优惠配置json
		JSONObject promotionJson = new JSONObject();
		if (promotionPrice != null && promotionPrice.getEnabled() && goodsPrice != null) {
			// 使用优惠,临时需求,需要只根据当天的时间进行判断是否可以优惠购买,不需要park服务端的优惠开始时间和结束时间
			Calendar calendar = Util.getCalendar();
			// 直接+8小时，将服务器时间转换为东八区，不能用于国际版
			// 结束时间
			calendar.add(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.add(Calendar.HOUR_OF_DAY, -8);
			Date endDate = calendar.getTime();
			// 开始时间
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.HOUR_OF_DAY, -8);
			// 根据配置文件设置为当天=0，获取前一天=-1，前几天=-x
			// 临时需求，照片需要查询三天内是否优惠，优惠过不能使用使用
			if (goodsPrice.getType() == ConstUtil.GOODS_TYPE_PHOTO) {
				calendar.add(Calendar.DAY_OF_MONTH, orderPromotionPhotoTime);
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, orderPromotionTime);
			}
			Date startDate = calendar.getTime();
			int fullPriceOrderCount = iOrderDao.getOrderFullPriceCount(userId, parkId, goodsPrice.getGoodsType(),
					startDate, endDate);
			int promotionOrderCount = iOrderDao.getOrderPromotionPriceCount(userId, parkId, goodsPrice.getGoodsType(),
					startDate, endDate);
			promotionJson.put("existPromotion", true);
			promotionJson.put("summary", promotionPrice.getSummary());
			promotionJson.put("description", promotionPrice.getDescription());
			if (promotionPrice.validateUsePromotion(fullPriceOrderCount, promotionOrderCount, timeZone)) {
				promotionJson.put("usePromotion", true);
				promotionJson.put("promotionPrice", promotionPrice.getPromotionPrice());
			} else {
				promotionJson.put("usePromotion", false);
				promotionJson.put("promotionPrice", promotionPrice.getPromotionPrice());
			}
			goodsPriceConfigJson.put("promotion", promotionJson);
		}
	}

	/**
	 * 
	 * @Description:整合游乐园后台配置的套餐信息
	 * @Time: 2019年10月11日下午1:48:45
	 * @return
	 */
	private void getCouponPriceConfig(JSONObject goodsPriceConfigJson, Integer goodsType, String userId, String parkId,
			ParkGoodsPrice parkGoodsPrice, String timeZone) {
		if (goodsType == ConstUtil.GOODS_TYPE_COUPON || goodsType == ConstUtil.GOODS_TYPE_CLIP
				|| goodsType == ConstUtil.GOODS_TYPE_PHOTO) {
			Optional<GoodsPriceConfiguration> oGoodsPriceConfiguration = parkGoodsPrice
					.getGoodsPriceConfig(ConstUtil.GOODS_TYPE_COUPON);
			oGoodsPriceConfiguration.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.PRICE_INCORRECT));
			CouponPrice coupon = oGoodsPriceConfiguration.get().getCouponPrice();
			if (goodsType == ConstUtil.GOODS_TYPE_COUPON) {
				Optional.ofNullable(coupon)
						.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.POST_PARK_COUPON_ERROR));
			}
			// 套餐配置json
			JSONObject couponPriceJson = new JSONObject();
			// 判断用户是否使用套票
			BuyerCoupon buyerCoupon = boughtService.getBuyerCoupon(userId, parkId);
			if (goodsType == ConstUtil.GOODS_TYPE_COUPON || (coupon != null && coupon.getGoodsTypes() != null
					&& coupon.getGoodsTypes().contains(goodsType))) {
				// 使用套票
				couponPriceJson.put("existCoupon", coupon.getEnabled());
				couponPriceJson.put("couponPrice", coupon.getCouponPrice() != null ? coupon.getCouponPrice() : null);
				// 套餐有效期
				couponPriceJson.put("inCouponPeriod", coupon.isInCouponPeriod());
				if (coupon.getEnabled() && coupon.isInCouponPeriod()) {
					// 获取时区
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, coupon.getUseDays() - 1);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
					simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
					couponPriceJson.put("couponExpireTime", simpleDateFormat.format(calendar.getTime()));
				}
				// 套餐id
				couponPriceJson.put("couponId", coupon.getCouponId());
				// 使用套票
				couponPriceJson.put("existCoupon", coupon.getEnabled());
				// 项目数量
				couponPriceJson.put("validGamesCount", coupon.getValidGamesCount());
				// 最高可省多少元
				couponPriceJson.put("maxOffers", coupon.getMaxOffers());
				couponPriceJson.put("summary", coupon.getSummary());
				couponPriceJson.put("description", coupon.getDescription());
				if (buyerCoupon != null) {
					couponPriceJson.put("useCoupon", true);
					// 使用范围0小视频、2：集锦3：照片
					couponPriceJson.put("canBuyGoodsType", buyerCoupon.getCanBuyGoodsTypes());
				} else {
					couponPriceJson.put("useCoupon", false);
					// 使用范围0小视频、2：集锦3：照片
					couponPriceJson.put("canBuyGoodsType", coupon.getGoodsTypes());
				}
			} else {
				couponPriceJson.put("existCoupon", false);
				if (buyerCoupon != null && buyerCoupon.getCanBuyGoodsTypes() != null
						&& buyerCoupon.getCanBuyGoodsTypes().contains(goodsType)) {
					couponPriceJson.put("useCoupon", true);
					// 使用范围0小视频、2：集锦3：照片
					couponPriceJson.put("canBuyGoodsType", buyerCoupon.getCanBuyGoodsTypes());
				} else {
					couponPriceJson.put("useCoupon", false);
				}
			}
			goodsPriceConfigJson.put("couponPrice", couponPriceJson);
		}
	}

	@Override
	public BoughtOrderResultResponse queryBoughtList(Map<String, Object> map) throws CustomException, Exception {
		ValidateUtil.validateEmptyAndInteger(map.get("pageNo"), EnumHandle.PAGENO_CAN_NOT_EMPTY);
		ValidateUtil.validateEmptyAndInteger(map.get("pageSize"), EnumHandle.PAGESIZE_CAN_NOT_EMPTY);
		ValidateUtil.validateEmptyAndString(map.get("userId"), EnumHandle.USERID_PARAM_INCORRECT);
		Integer pageNo = Integer.parseInt(map.get("pageNo").toString());
		Integer pageSize = Integer.parseInt(map.get("pageSize").toString());
		map.put("pageNo", (pageNo - 1) * pageSize);
		List<BoughtOrderDo> boughtOrderList = iOrderDao.queryBoughtList(map);
		// 生成返回数据
		BoughtOrderResultResponse result = new BoughtOrderResultResponse();
		List<BoughtOrderResponse> list = new ArrayList<>();
		BoughtOrderResponse boughtOrderResponse = null;
		String thumbnailUrl = null;
		for (BoughtOrderDo bought : boughtOrderList) {
			if (!StringUtils.isEmpty(bought.getThumnailUrl())) {
				// 电子照片和打印照片,thumbnail地址其实是下载地址
				if (bought.getGoodsType() == ConstUtil.GOODS_TYPE_PHOTO
						|| bought.getGoodsType() == ConstUtil.GOODS_TYPE_PHOTOPRINT) {
					if (bought.getItemStatus() == ConstUtil.STATUS_NOT_MOVE) {
						thumbnailUrl = s3ObjectStorageServiceClient.convertCDNUrl(1, bought.getThumnailUrl());
					} else if (bought.getItemStatus() == ConstUtil.STATUS_MOVE) {
						thumbnailUrl = s3ObjectStorageServiceClient.getSignUrl(2, bought.getThumnailUrl());
					}
				} else {
					if (bought.getItemStatus() == ConstUtil.STATUS_NOT_MOVE) {
						thumbnailUrl = s3ObjectStorageServiceClient.convertCDNUrl(1, bought.getThumnailUrl());
					} else if (bought.getItemStatus() == ConstUtil.STATUS_MOVE) {
						thumbnailUrl = s3ObjectStorageServiceClient.convertCDNUrl(2, bought.getThumnailUrl());
					}
				}
			}
			boughtOrderResponse = new BoughtOrderResponse(bought.getOrderId(), bought.getAmusementparkId(),
					bought.getParkName(), bought.getGoodsName() != null ? bought.getGoodsName() : null,
					bought.getGoodsId(), bought.getGoodsType(),
					bought.getResourceCategory() != null ? bought.getResourceCategory() : null,
					bought.getResourceType() != null ? bought.getResourceType() : null,
					bought.getGameName() != null ? bought.getGameName() : null, bought.getOrderType(),
					bought.getPaymentTime(), bought.getStatus(), bought.getItemStatus(), thumbnailUrl, null);
			if (bought.getGoodsType() == ConstUtil.GOODS_TYPE_COUPON) {
				boughtOrderResponse.setGameName(
						Util.formateTime(bought.getPaymentTime()).append(bought.getGoodsName()).toString());
				Order order = orderRepository.getOrder(bought.getOrderId());
				Date couponEndDate = order.stream()
						.filter(orderItem -> orderItem.getGoods().goodsType() == ConstUtil.GOODS_TYPE_COUPON)
						.findFirst().map(oi -> (CouponGoods) oi.getGoods()).map(couponGoods -> couponGoods.getEndDate())
						.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.PAID_COUPON_ERROR));
				boughtOrderResponse.setCouponEndTime(couponEndDate);
			}
			list.add(boughtOrderResponse);
		}
		// 插入列表
		result.setList(list);
		// 插入总数
		Integer total = iOrderDao.queryBoughtTotal(map);
		result.setTotal(total);
		return result;
	}
}
