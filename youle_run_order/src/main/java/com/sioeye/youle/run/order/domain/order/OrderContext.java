package com.sioeye.youle.run.order.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import org.springframework.util.StringUtils;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.context.CreateOrderRequest;
import com.sioeye.youle.run.order.context.CreateOrderResponse;
import com.sioeye.youle.run.order.context.OrderGoods;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.goods.Game;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.order.event.PlacedOrderEventMessage;
import com.sioeye.youle.run.order.domain.payment.Payment;
import com.sioeye.youle.run.order.domain.payment.PaymentResult;
import com.sioeye.youle.run.order.domain.price.Currency;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.price.GoodsPriceConfiguration;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import com.sioeye.youle.run.order.domain.price.ShareActivityPrice;
import com.sioeye.youle.run.order.domain.timezone.ParkTimeZone;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderContext {

	private Currency currency = Currency.buildDefault();
	private ParkTimeZone parkTimeZone = ParkTimeZone.buildDefault();
	private CreateOrderRequest request;
	private ParkGoodsPrice parkGoodsPrice;
	private Order order;
	private PaymentResult paymentResult;
	private BiFunction<String, String, ParkGoodsPrice> parkGoodsPriceFunction;
	private ValidateCalculateGoodsPromotionFunction validateCalculateGoodsPromotionFunction;
	private Map<String, CompletableFuture<ParkGoodsPrice>> priceMap = new HashMap<>(3);
	private OrderGoodsMappingContext goodsMappingContext;


	public OrderContext(CreateOrderRequest request, BiFunction<String, String, ParkGoodsPrice> parkGoodsPriceFunction,
			ValidateCalculateGoodsPromotionFunction validateCalculateGoodsPromotionFunction) {
		setRequest(request);
		this.parkGoodsPriceFunction = parkGoodsPriceFunction;
		this.validateCalculateGoodsPromotionFunction = validateCalculateGoodsPromotionFunction;
		callParkGoodsPrice(parkGoodsPriceFunction);
		Buyer buyer = new Buyer(request.getUserId(), request.getOpenId());
		OrderAmount orderAmount = new OrderAmount(request.getOriginalAmount(), request.getActualAmount(), currency);

		OrderDevice orderDevice = new OrderDevice(request.getDeviceRecordId(), request.getDeviceType());

		// 如果场景值存在值，就以场景值为订单id
		if (StringUtils.hasText(request.getSceneId())) {
			order = new Order(request.getSceneId(), orderAmount, request.getPromotionType(), request.getPayWay(), buyer,
					parkTimeZone, goodsMappingContext.getOrderType(), orderDevice, request.getSearchId());
		} else {
			order = new Order(OrderIdUtils.uuid(), orderAmount, request.getPromotionType(), request.getPayWay(), buyer,
					parkTimeZone, goodsMappingContext.getOrderType(), orderDevice, request.getSearchId());
		}

	}

	public CreateOrderRequest getRequest() {
		return request;
	}

	public String parkId() {
		return request.getAmusementParkId();
	}

	public String userId() {
		return request.getUserId();
	}
	public Integer orderType() {
		return order.getType();
	}
	public void useCoupon(BuyerCoupon buyerCoupon){
		order.useCoupon(buyerCoupon);
	}
	public List<GoodsContext> goodsList() {
		// TODO Map resourceType to goodsType
		// return request.getGoodsList().stream().map((g) -> (GoodsContext)
		// g).collect(Collectors.toList());
		return goodsMappingContext.getGoodsMappingResourceList().stream().map(goods -> (GoodsContext) goods)
				.collect(Collectors.toList());
	}

	public List<GoodsMappingResource> goodsMappingResourcesList() {
		return goodsMappingContext.getGoodsMappingResourceList();
	}

	public PromotionTypeEnum getPromotionType() {
		return request.getPromotionType();
	}

	public void addGoods(Goods goods) {
		if (goods == null) {
			throw CustomException.build(DomainErrorCodeEnum.INVALID_GOODS);
		}
		if (!goods.getPark().id().equals(request.getAmusementParkId())) {
			throw new CustomException(DomainErrorCodeEnum.GOODS_PARKID_NOT_EQUAL_REQUEST_PARKID_SOLD_OUT.getCode(),
					String.format(DomainErrorCodeEnum.GOODS_PARKID_NOT_EQUAL_REQUEST_PARKID_SOLD_OUT.getMessage(),
							goods.getPark().id(), request.getAmusementParkId()));
		}
		boolean checkNeedCalcAmount = goodsMappingContext.checkNeedCalcAmount(goods.getType());
		synchronized (this) {
			// 共享资源，加锁
			String gameId = goods.getGame() == null ? null : goods.getGame().id();
			String key = calcPirceKey(goods.getPark().id(), Optional.ofNullable(gameId));
			priceMap.computeIfAbsent(key, mapKey -> CompletableFuture
					.supplyAsync(() -> parkGoodsPriceFunction.apply(goods.getPark().id(), gameId)));

			OrderItem item = OrderItem.createItem(goods, 1,
					getOrderItemShareActivty(request.getParkShareActivityId(), goods.getType(), goods.getGame()),
					new Date(), this::getPrice, this::calcGoodsActualAmount, checkNeedCalcAmount);
			order.addOrderItem(item);
		}

	}

	public void validateOriginalAmount() {
		order.validateOriginalAmount();
	}

	public void validateActualAmount(BigDecimal promotionActual) {
		order.validateActualAmount(promotionActual);
	}

	public CreateOrderResponse toResponse() {
		Goods goods = order.getFirstGoods();
		CreateOrderResponse createOrderResponse = new CreateOrderResponse();
		createOrderResponse.setActivityId(order.getActivity());
		createOrderResponse.setActualAmount(order.getOrderAmount().getActualAmount());
		createOrderResponse.setOriginalAmount(order.getOrderAmount().getOriginalAmount());
		createOrderResponse.setAmusementParkId(goods.getPark().id());
		createOrderResponse.setOpenId(order.getBuyer().getOpenId());
		createOrderResponse.setOrderTime(order.getPlaceOrderDate());
		createOrderResponse.setCount(order.getOrderItemCount());
		createOrderResponse.setOrderId(order.id());
		createOrderResponse.setStatus(order.getOrderStatus().getCode());
		createOrderResponse.setAppId(paymentResult.getAppId());
		createOrderResponse.setPrepayId(paymentResult.getPrePaymentId());
		createOrderResponse.setSign(paymentResult.getPaySign());
		createOrderResponse.setTradeType(paymentResult.getTradeType());
		createOrderResponse.setPayWay(paymentResult.getPayWay());
		createOrderResponse.setResultDate(paymentResult);
		return createOrderResponse;
	}

	public Payment toPayment() {
		String paymentDescription = goodsMappingContext.getGoodsPricePaymentDescription(
				order.getPark() != null ? order.getPark().getParkName() : null,
				order.getGame().isPresent() ? order.getGame().get().getGameName() : null);
		Payment payment = new Payment(request.getPayWay(), null, order.getBuyer(), order.getPark().id(),
				order.getPark().getParkName(), order.id(), paymentDescription, paymentDescription,
				request.getSpbillCreateIp(), order.getOrderAmount().getActualAmount());
		return payment;
	}

	public void applyPaymentResult(PaymentResult paymentResult) {
		this.paymentResult = paymentResult;
		order.savePaymentId(paymentResult.getPaymentId());
	}

	public Order toOrder() {
		return order;
	}

	public PlacedOrderEventMessage toPlacedOrderEvent() {
		// 转换request时，如果为空，已经构建了searchId
		// if (!StringUtils.hasText(order.getSearchId())){
		// order.saveSearchId(OrderIdUtils.combineSearchId());
		// }
		return order.toPlacedOrderEvent();

	}

	/**
	 * 整个订单是否需要验证商品重复购买
	 *
	 * @return true 需要验证 false 不需要验证
	 */
	public boolean needValidateDuplicate() {
		return goodsMappingContext.isNeedValidateDuplicate();
	}

	/**
	 * 整个订单是否需要验证商品上架 赠送商品不需要验证上下架
	 *
	 * @return true 需要验证 false 不需要
	 */
	public boolean needValidateOnShelf(Integer goodsType) {

		Optional<Boolean> onShelf = request.getGoodsList().stream().filter(goods -> goodsType.equals(goods.goodsType()))
				.map(OrderGoods::getNeedValidateOnShelf).findAny();
		return onShelf.orElse(false);

	}

	/**
	 * 计算商品的实际金额
	 *
	 * @param goodsContext
	 * @param goodsPrice
	 * @return
	 */
	private BigDecimal calcGoodsActualAmount(GoodsContext goodsContext, BigDecimal goodsPrice) {
		String priceKey = calcPirceKey(this.order.getPark().id(), this.order.getGame().map(game -> game.id()));
		CompletableFuture<ParkGoodsPrice> completableFuture = priceMap.get(priceKey);
		if (completableFuture != null) {
			return this.validateCalculateGoodsPromotionFunction.apply(
					buildPromotionContext(this.order.getPromotionType()), goodsContext, completableFuture.join(),
					this);
		}
		log.info(String.format("priceKey(%s) is not exist.", priceKey));
		return goodsPrice;
	}

	/**
	 * 获取不同优惠的优惠id
	 *
	 * @param promotionType
	 * @return
	 */
	private PromotionContext buildPromotionContext(PromotionTypeEnum promotionType) {
		// 现阶段只有分享活动才需要优惠id，后续套餐有可能也需要优惠id
		if (promotionType.getCode() == PromotionTypeEnum.SHARE.getCode()) {
			return new PromotionContext(promotionType, getRequest().getParkShareActivityId());
		}
		return new PromotionContext(promotionType, null);
	}

	private BigDecimal getPrice(String parkId, String gameId, Integer goodsType) {
		Optional<GoodsPrice> goodsPrice = getGoodsPrice(parkId, gameId, goodsType);
		return goodsPrice.map(GoodsPrice::getPrice).orElse(BigDecimal.ZERO);
	}

	public GoodsPrice checkGoodsIsOnShelf(Integer goodsType) {
		Optional<GoodsPrice> goodsPrice = getGoodsPrice(goodsType);
		if (!goodsPrice.isPresent() && needValidateOnShelf(goodsType)) {
			throw new CustomException(DomainErrorCodeEnum.GOODS_SOLD_OUT.getCode(), String
					.format(DomainErrorCodeEnum.GOODS_SOLD_OUT.getMessage(), "goodsType:" + goodsType.toString()));
		}
		return goodsPrice.orElse(null);
	}

	private Optional<GoodsPrice> getGoodsPrice(Integer goodsType) {
		if (this.parkGoodsPrice == null) {
			// this.parkGoodsPrice不为空时，说明异步获取admin-park-new的价格还没有返回；需要join等待返回
			CompletableFuture<ParkGoodsPrice> completableFuture = priceMap
					.get(calcPirceKey(request.getAmusementParkId(), Optional.ofNullable(request.getGameId())));
			ParkGoodsPrice join = completableFuture.join();
			setParkGoodsPrice(join);
		}
		return parkGoodsPrice.getPriceList().stream().map(GoodsPriceConfiguration::getGoodsPrice)
				.filter((g) -> g.getStatus() && g.getType().equals(goodsType)).findAny();
	}

	private Optional<GoodsPrice> getGoodsPrice(String parkId, String gameId, Integer goodsType) {
		CompletableFuture<ParkGoodsPrice> completableFuture = priceMap
				.get(calcPirceKey(parkId, Optional.ofNullable(gameId)));
		ParkGoodsPrice join = completableFuture.join();
		return join.getPriceList().stream().map(GoodsPriceConfiguration::getGoodsPrice)
				.filter((g) -> g.getStatus() && g.getType().equals(goodsType)).findAny();
	}

	private OrderItemShareActivty getOrderItemShareActivty(String shareActivityId, Integer goodsType, Game game) {
		if (!StringUtils.hasText(shareActivityId)) {
			return null;
		}
		if (game == null) {
			return null;
		}
		Optional<ShareActivityPrice> shareActivityPrice = parkGoodsPrice.getShareActivityPrice(goodsType,
				shareActivityId);

		ShareActivityPrice price = shareActivityPrice
				.orElseThrow(() -> new CustomException(EnumHandle.SHARE_PRICE_NOT_SET));
		if (price.canUseShareActivity()) {

			OrderItemShareActivty orderItemShareActivty = new OrderItemShareActivty(price.getThirdShareId(),
					price.getShareActivityId(), price.checkGameCanUploadShare(game.id()), price.getUploadApi(),
					price.getShareLinkApi());
			return orderItemShareActivty;
		}
		return null;
	}

	private void callParkGoodsPrice(BiFunction<String, String, ParkGoodsPrice> parkGoodsPriceFunction) {

		CompletableFuture<ParkGoodsPrice> completableFuture = CompletableFuture
				.supplyAsync(() -> parkGoodsPriceFunction.apply(request.getAmusementParkId(), request.getGameId()));
		priceMap.put(calcPirceKey(request.getAmusementParkId(), Optional.ofNullable(request.getGameId())),
				completableFuture);
		// completableFuture.thenApply(price -> {
		// setParkGoodsPrice(price);
		// goodsMappingContext = OrderGoodsMappingContext.build(price,
		// request.getGoodsList());
		// return Void.TYPE;
		// });
		try {
			ParkGoodsPrice parkGoodsPrice = completableFuture.get();
			setParkGoodsPrice(parkGoodsPrice);
			goodsMappingContext = OrderGoodsMappingContext.build(parkGoodsPrice, request.getGoodsList());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	private void setParkGoodsPrice(ParkGoodsPrice parkGoodsPrice) {
		if (parkGoodsPrice == null) {
			throw new RuntimeException("the park goods price is null.");
		}
		this.parkGoodsPrice = parkGoodsPrice;
		// 每个商品初始化对应的园区id和园区名称
		parkGoodsPrice.getPriceList().forEach(g -> {
			g.getGoodsPrice().setParkId(parkGoodsPrice.getParkId());
			g.getGoodsPrice().setParkName(parkGoodsPrice.getParkName());
		});
	}

	private String calcPirceKey(String parkId, Optional<String> gameId) {
		return parkId + gameId.orElse("");
	}

	private void setRequest(CreateOrderRequest request) {
		if (request == null) {
			throw CustomException.build(DomainErrorCodeEnum.PARAMS_INCORRECT);
		}
		this.request = request;
	}
}
