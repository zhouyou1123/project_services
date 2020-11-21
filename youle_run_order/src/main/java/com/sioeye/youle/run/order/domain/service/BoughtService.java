package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.goods.GameCouponGoods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;
import com.sioeye.youle.run.order.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Component
public class BoughtService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private PaymentService paymentService;
	/**
	 * 30分钟后就不查询未支付的订单
	 */
	private long paybackDuration = 30 * 60 * 1000;

	public Order getOrder(String orderId) {
		return orderRepository.getOrder(orderId);

	}

	public Optional<Order> getOrderByUserId(String orderId, String userId) {
		Order order = orderRepository.getOrderByUser(orderId, userId);
		// 对Order做一下状态验证
		return this.checkIsBoughtOrder(order);
	}

	/**
	 * 获取个人全园套餐
	 * @param buyerId 用户id
	 * @param parkId 游乐园id
	 * @return
	 */
	public BuyerCoupon getBuyerCoupon(String buyerId, String parkId) {
		Date currentDate = new Date();
		// 首先查询个人套票记录，并判断是否有效；
		BuyerCoupon buyerCoupon = orderRepository.getBuyerCoupon(buyerId, parkId);
		if (buyerCoupon != null && !buyerCoupon.validatePeriodOverdue(currentDate)) {
			return buyerCoupon;
		}
		// 判断已购买套票并且订单id不相等
		String couponOrderId = buyerCoupon == null ? "" : buyerCoupon.getOrderId();
		BuyerCoupon newBuyerCoupon = null;
		Order orderBySuccess = orderRepository.getLastPaidOrderByParkId(buyerId, parkId, GoodsTypeEnum.COUPON.getCode());
		if (orderBySuccess != null && !orderBySuccess.id().equals(couponOrderId)) {
			// 判断已购订单的套票周期是否有效
			newBuyerCoupon = this.createBuyerCoupon(orderBySuccess);
			if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
				return newBuyerCoupon;
			}
		}
		// 获取未支付订单，并到第三方支付机构进行查询
		Order lastOrderByUser = orderRepository.getLastOrderByParkId(buyerId, parkId, GoodsTypeEnum.COUPON.getCode());
		if (lastOrderByUser == null || lastOrderByUser.id().equals(couponOrderId)) {
			return null;
		}

		if (lastOrderByUser.getOrderStatus().equals(OrderStatusEnum.PAID)) {
			// 订单状态为已支付就直接返回，上一步已经查询过了，当然有可能也是有一点时间差
			newBuyerCoupon = this.createBuyerCoupon(lastOrderByUser);
			if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
				return newBuyerCoupon;
			}
		}
		// 调用payment服务查询第三方支付机构的状态
		if (lastOrderByUser.getOrderAmount().getActualAmount().compareTo(BigDecimal.ZERO) > 0
				&& !paymentService.validatePaid(lastOrderByUser.id(), lastOrderByUser.getPaymentId().id(), true)) {
			return null;
		}
		newBuyerCoupon = this.createBuyerCoupon(lastOrderByUser);
		if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
			return newBuyerCoupon;
		}
		return null;
	}

	private BuyerCoupon createBuyerCoupon(Order order) {
		if (order.getFirstGoods().goodsType() == GoodsTypeEnum.COUPON.getCode()){
			CouponGoods couponGoods = (CouponGoods) order.getFirstGoods();
			BuyerCoupon newBuyerCoupon = new BuyerCoupon(order.getBuyer(), couponGoods.getStartDate(), couponGoods.getEndDate(),
					couponGoods.getTimeZone(), order.id(), couponGoods.getPark(), couponGoods.getCanBuyGoodsTypes(),0,null,couponGoods.goodsId());
			return newBuyerCoupon;
		}else {
			GameCouponGoods couponGoods = (GameCouponGoods) order.getFirstGoods();
			BuyerCoupon newBuyerCoupon = new BuyerCoupon(order.getBuyer(), couponGoods.getStartDate(), couponGoods.getEndDate(),
					couponGoods.getTimeZone(), order.id(), couponGoods.getPark(), couponGoods.getCanBuyGoodsTypes(),1, couponGoods.getGame().id(),couponGoods.goodsId());
			return newBuyerCoupon;
		}

	}
	public BuyerCoupon getBuyerGameCoupon(String buyerId, String goodsId) {
		Date currentDate = new Date();
		// 首先查询个人套票记录，并判断是否有效；
		BuyerCoupon buyerCoupon = orderRepository.getBuyerGameCoupon(buyerId, goodsId);
		if (buyerCoupon != null && !buyerCoupon.validatePeriodOverdue(currentDate)) {
			return buyerCoupon;
		}
		// 判断已购买套票并且订单id不相等
		String couponOrderId = buyerCoupon == null ? "" : buyerCoupon.getOrderId();
		BuyerCoupon newBuyerCoupon = null;
		Order orderBySuccess = orderRepository.getLastPaidOrderByGoodsId(buyerId, goodsId, GoodsTypeEnum.GAMECOUPON.getCode());
		if (orderBySuccess != null && !orderBySuccess.id().equals(couponOrderId)) {
			// 判断已购订单的套票周期是否有效
			newBuyerCoupon = this.createBuyerCoupon(orderBySuccess);
			if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
				return newBuyerCoupon;
			}
		}
		// 获取未支付订单，并到第三方支付机构进行查询
		Order lastOrderByUser = orderRepository.getLastOrderByGoodsId(buyerId, goodsId, GoodsTypeEnum.GAMECOUPON.getCode());
		if (lastOrderByUser == null || lastOrderByUser.id().equals(couponOrderId)) {
			return null;
		}

		if (lastOrderByUser.getOrderStatus().equals(OrderStatusEnum.PAID)) {
			// 订单状态为已支付就直接返回，上一步已经查询过了，当然有可能也是有一点时间差
			newBuyerCoupon = this.createBuyerCoupon(lastOrderByUser);
			if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
				return newBuyerCoupon;
			}
		}
		// 调用payment服务查询第三方支付机构的状态
		if (lastOrderByUser.getOrderAmount().getActualAmount().compareTo(BigDecimal.ZERO) > 0
				&& !paymentService.validatePaid(lastOrderByUser.id(), lastOrderByUser.getPaymentId().id(), true)) {
			return null;
		}
		newBuyerCoupon = this.createBuyerCoupon(lastOrderByUser);
		if (!newBuyerCoupon.validatePeriodOverdue(currentDate)) {
			return newBuyerCoupon;
		}
		return null;
	}

	public BuyerCoupon getBuyerGameCouponByGameId(String buyerId, String parkId,String gameId) {
		Date currentDate = new Date();
		// 首先查询个人套票记录，并判断是否有效；
		BuyerCoupon buyerCoupon = orderRepository.getBuyerGameCouponByGameId(buyerId, parkId,gameId);
		if (buyerCoupon != null && !buyerCoupon.validatePeriodOverdue(currentDate)) {
			return buyerCoupon;
		}
		return null;
	}

	/**
	 * 检测用户是否购买指定商品 1、查询已支付 2、查询未支付记录 3、未支付记录下单时间30分钟之前返回空
	 * 4、未支付记录下单时间与当前时间比，小于30分钟需要调微信接口进行查询（防止微信支付了，我们系统还没有收到支付回调通知） 5、都不满足返回空
	 * 
	 * @param userId
	 * @param goodsId
	 * @param goodsType
	 * @return
	 */
	public Optional<Order> checkIsBoughtGoods(String userId, String goodsId, Integer goodsType) {
		Order orderByPaid = orderRepository.getLastPaidOrderByGoodsId(userId, goodsId, goodsType);
		if (orderByPaid != null) {
			return Optional.of(orderByPaid);
		}
		Order lastOrderByNotPay = orderRepository.getLastOrderByGoodsId(userId, goodsId, goodsType);
		if (lastOrderByNotPay == null) {
			return Optional.ofNullable(lastOrderByNotPay);
		}
		if (OrderStatusEnum.PAID.getCode() == lastOrderByNotPay.getOrderStatus().getCode()) {
			return Optional.of(lastOrderByNotPay);
		}
		if (new Date().getTime() >= lastOrderByNotPay.getPlaceOrderDate().getTime() + paybackDuration) {
			return Optional.ofNullable(null);
		}
		if (paymentService.validatePaid(lastOrderByNotPay.id(), lastOrderByNotPay.getPaymentId().id(), true)) {
			return Optional.of(lastOrderByNotPay);
		}
		return Optional.ofNullable(null);
	}

	/**
	 * 检查用户是否对商品下单
	 * 
	 * @param userId
	 * @param goodsId
	 * @param goodsType
	 * @return Optional<Order>
	 */
	public Optional<Order> checkIsGoods(String userId, String goodsId, Integer goodsType) {
		Order orderByPaid = orderRepository.getLastPaidOrderByGoodsId(userId, goodsId, goodsType);
		if (orderByPaid != null) {
			return Optional.of(orderByPaid);
		}
		Order lastOrderByNotPay = orderRepository.getLastOrderByGoodsId(userId, goodsId, goodsType);
		if (lastOrderByNotPay != null) {
			if (paymentService.validatePaid(lastOrderByNotPay.id(), lastOrderByNotPay.getPaymentId().id(), true)) {
				return Optional.of(lastOrderByNotPay);
			}
			return Optional.of(lastOrderByNotPay);
		}
		return Optional.ofNullable(null);
	}

	/**
	 * 检查订单状态 1、验证订单是否为已支付 2、验证订单是否已超时
	 * 3、未支付记录下单时间与当前时间比，小于30分钟需要调微信接口进行查询（防止微信支付了，我们系统还没有收到支付回调通知） 4、都不满足返回空
	 * 
	 * @param order
	 *            return Optional<Order>
	 */
	public Optional<Order> checkIsBoughtOrder(Order order) {
		if (OrderStatusEnum.PAID.getCode() == order.getOrderStatus().getCode()) {
			return Optional.of(order);
		}
		if (new Date().getTime() >= order.getPlaceOrderDate().getTime() + paybackDuration) {
			return Optional.ofNullable(null);
		}
		if (paymentService.validatePaid(order.id(), order.getPaymentId().id(), true)) {
			return Optional.of(order);
		}
		return Optional.ofNullable(null);
	}
}
