package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.common.AbstractId;
import com.sioeye.youle.run.order.domain.common.Entity;
import com.sioeye.youle.run.order.domain.goods.*;
import com.sioeye.youle.run.order.domain.order.event.PlacedOrderEventMessage;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import com.sioeye.youle.run.order.domain.payment.PaymentId;
import com.sioeye.youle.run.order.domain.timezone.ParkTimeZone;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
public class Order extends Entity implements Iterable<OrderItem> {

	private String orderNo;
	private OrderAmount orderAmount;
	private Buyer buyer;
	private Date placeOrderDate;
	private List<OrderItem> orderItemList;
	private PromotionTypeEnum promotionType;
	private PaymentId paymentId;
	private OrderStatusEnum orderStatus;
	private Date paymentDate;
	private ParkTimeZone timeZone;
	private PayWayEnum payWay;
	private Integer type;
	private Integer orderType;
	private OrderDevice device;
	private String searchId;
	private BuyerCoupon buyerCoupon;


	/**
	 * 转换model里的order为domain里的order
	 * 
	 * @param id
	 * @param buyer
	 */
	public Order(String id, Buyer buyer) {
		super(id);
		setBuyer(buyer);
	}

	public Order(String id, OrderAmount orderAmount, PromotionTypeEnum promotionType, PayWayEnum payWay, Buyer buyer,
			ParkTimeZone timeZone, Integer type, OrderDevice device,String searchId) {
		this(id, orderAmount, promotionType, payWay, buyer, new Date(), timeZone, null, OrderStatusEnum.NOT_PAY, null,
				type, null, device,searchId);
	}

	public Order(String id, OrderAmount orderAmount, PromotionTypeEnum promotionType, PayWayEnum payWay, Buyer buyer,
			Date placeOrderDate, ParkTimeZone timeZone, PaymentId paymentId, OrderStatusEnum orderStatus,
			Date paymentDate, Integer type, Integer orderType, OrderDevice device,String searchId) {
		super(id);
		setBuyer(buyer);
		setTimeZone(timeZone);
		setOrderAmount(orderAmount);
		this.placeOrderDate = placeOrderDate;
		this.promotionType = promotionType;
		this.payWay = payWay;
		orderItemList = new ArrayList<>();
		this.orderStatus = orderStatus;
		this.paymentId = paymentId;
		this.paymentDate = paymentDate;
		this.type = type;
		this.orderType = orderType;
		this.device = device;
		this.searchId=searchId;
	}

	public void paidBack(Date paymentDate) {
		orderStatus = OrderStatusEnum.PAID;
		this.paymentDate = paymentDate;
	}

	public void beginfiling(Function<Goods, GoodsResourceUrl> resourceFunction) {
		Iterator<OrderItem> iterator = this.iterator();
		while (iterator.hasNext()) {
			OrderItem orderItem = iterator.next();
			if (orderItem.getGoods() instanceof ResourceGoods || orderItem.getGoods() instanceof PrintGoods) {
				GoodsResourceUrl goodsResource = resourceFunction.apply(orderItem.getGoods());
				orderItem.beginFiling(goodsResource.getPreviewUrl(), goodsResource.getDownloadUrl(),
						goodsResource.getThumbnailUrl());
			} else {
				orderItem.beginFiling(null, null, null);
			}
		}
	}

	public void filingBack() {
		Iterator<OrderItem> iterator = this.iterator();
		while (iterator.hasNext()) {
			OrderItem orderItem = iterator.next();
			orderItem.endFiling();
		}
	}

	public void savePaymentId(PaymentId paymentId) {
		this.paymentId = paymentId;
	}

	public void saveSearchId(String searchId){
		this.searchId = searchId;
	}

	public PlacedOrderEventMessage toPlacedOrderEvent(){
		PlacedOrderEventMessage.PlacedOrderEventMessageBuilder notificationMessageBuilder = PlacedOrderEventMessage.builder().orderId(this.id()).userId(this.getBuyer().id())
				.parkId(this.getPark().id()).placeOrderDate(this.getPlaceOrderDate()).paymentDate(this.getPaymentDate()).combineSearchId(this.getSearchId())
				.deviceType(this.getDevice().getDeviceType().getCode());
		List<PlacedOrderEventMessage.Goods> goodsList = new ArrayList<>(this.getOrderItemCount());
		for (OrderItem item: getOrderItemList() ){
			if(item.getGoods() instanceof ResourceGoods){
				ResourceGoods resourceGoods = (ResourceGoods)item.getGoods();

				goodsList.add(new PlacedOrderEventMessage.Goods(item.getGoods().id(),item.getGoods().getType(),resourceGoods.id(),resourceGoods.getResourceType(),resourceGoods.getResourceCategory().getCode()));
			}else{
				goodsList.add(new PlacedOrderEventMessage.Goods(item.getGoods().id(),item.getGoods().getType(),null,null,null));
			}
		}
		return notificationMessageBuilder.goodsList(goodsList).build();
	}

	public Goods getFirstGoods() {
		return getFirstOrderItem().getGoods();
	}

	public Optional<Game> getGame() {
		return orderItemList.stream().filter(item -> item.getGoods().getGame() != null).map(OrderItem::getGoods)
				.map(Goods::getGame).findAny();
	}

	public Park getPark() {
		return getFirstOrderItem().getGoods().getPark();
	}

	public void useCoupon(BuyerCoupon buyerCoupon){
		if ( PromotionTypeEnum.COUPON.getCode() != promotionType.getCode()){
			throw new RuntimeException("promotionType is not COUPON ,can not use coupon.");
		}
		this.buyerCoupon = buyerCoupon;
	}

	/**
	 * 验证原始价格
	 */
	public void validateOriginalAmount() {

		BigDecimal originalAmount = calcOriginalAmount();
		setOrderAmount(new OrderAmount(originalAmount, this.orderAmount.getActualAmount(),
				this.getOrderAmount().getCurrency()));
		this.orderAmount.validateOriginalAmount(originalAmount);

	}

	public void validateActualAmount(BigDecimal promotionAmount) {
		if (promotionAmount == null) {
			promotionAmount = BigDecimal.ZERO;
		}
		BigDecimal sum = orderItemList.stream().map(o -> o.getActualAmount()).reduce(BigDecimal.ZERO,
				(a, b) -> a.add(b == null ? BigDecimal.ZERO : b));
		this.orderAmount.validateActualAmount(sum.subtract(promotionAmount));
	}

	/**
	 * 验证商品类型是否能购买
	 * 
	 * @param canBuyGoodsTypeList
	 */
	public void validateGoodsTypeInRange(Collection<Integer> canBuyGoodsTypeList) {
		for (OrderItem item : orderItemList) {
			if (!canBuyGoodsTypeList.stream().anyMatch(type -> type.equals(item.getGoods().getType()))) {
				throw new CustomException(DomainErrorCodeEnum.INVALID_GOODSTYPE.getCode(),
						DomainErrorCodeEnum.COUPON_NOTIN_PERIOD.getMessage());
			}
		}
	}

	public PromotionTypeEnum getPromotionType() {
		return promotionType;
	}

	public String getActivity() {
		return orderItemList.stream().filter(item -> item.getGoods().activity() != null)
				.map(item -> item.getGoods().activity()).map(AbstractId::id).findAny().orElse(null);
	}

	public Integer getType() {
		return this.type;
	}

	public OrderItemShareActivty getParkShareActivity() {
		return getFirstOrderItem().getShareActivty();
	}

	public void UpdateParkShareActivity(String shareUrl) {
		OrderItemShareActivty shareActivty = getFirstOrderItem().getShareActivty();
		OrderItemShareActivty newShareActivity = new OrderItemShareActivty(shareActivty.getShareId(),
				shareActivty.getParkShareId(), shareActivty.getUploadFlag(), shareActivty.getShareUploadUrl(),
				shareActivty.getShareCheckUrl());
		getFirstOrderItem().updateShareActivity(newShareActivity);
	}

	public boolean checkCanUpdateShareActivity() {
		if (getFirstOrderItem().getShareActivty() != null
				&& !StringUtils.hasText(getFirstOrderItem().getShareActivty().getShareUploadUrl())) {
			return true;
		} else {
			return false;
		}
	}

	public void addOrderItem(OrderItem item) {
		orderItemList.add(item);
		// BigDecimal originalAmount = calcOriginalAmount();
		// setOrderAmount(new
		// OrderAmount(originalAmount,this.orderAmount.getActualAmount(),this.getOrderAmount().getCurrency()));
	}

	public Integer getOrderItemCount() {
		return orderItemList.size();
	}

	public OrderItem getFirstOrderItem() {
		return orderItemList.get(0);
	}

	private BigDecimal calcOriginalAmount() {
		return orderItemList.stream().filter(OrderItem::getNeedCalcAmount).map(o -> o.getOriginalAmount())
				.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
	}

	private void setBuyer(Buyer buyer) {
		assertArgumentNotNull(buyer, "the buyer of order is not null.");
		this.buyer = buyer;
	}

	private void setOrderAmount(OrderAmount orderAmount) {
		assertArgumentNotNull(orderAmount, "the amount of order is not null.");
		this.orderAmount = orderAmount;
	}

	private void setTimeZone(ParkTimeZone timeZone) {
		assertArgumentNotNull(timeZone, "the timeZone of order is not null.");
		this.timeZone = timeZone;
	}

	@Override
	public Iterator<OrderItem> iterator() {
		return orderItemList.iterator();
	}

	public Stream<OrderItem> stream() {
		return orderItemList.stream();
	}
}
