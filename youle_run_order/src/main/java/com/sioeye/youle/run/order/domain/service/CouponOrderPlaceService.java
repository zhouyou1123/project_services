package com.sioeye.youle.run.order.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.CouponOrder;
import com.sioeye.youle.run.order.domain.order.Order;

@Component
public class CouponOrderPlaceService {

	@Autowired
	private OrderRepository orderRepository;

	public void placeOrderCoupon(BuyerCoupon buyerCoupon, String goodsId, GoodsTypeEnum goodsType, Order goodsOrder) {
		CouponOrder couponOrder = new CouponOrder(buyerCoupon, goodsId, goodsType, goodsOrder);
		orderRepository.saveCouponOrder(couponOrder);
	}
}
