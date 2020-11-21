package com.sioeye.youle.run.order.domain.order;

import java.util.Date;

import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.common.AbstractId;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;

import lombok.Getter;

@Getter
public class CouponOrder extends AbstractId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String couponId;// 套餐id
	private String couponOrderId;// 套餐订单id
	private String userId;
	private String goodsId;
	private GoodsTypeEnum goodsType;
	private Order goodsOrder;// 套餐购买商品order
	private Integer status;
	private Date createTime;
	private Date updateTime;
	private Integer level;

	public CouponOrder(BuyerCoupon buyerCoupon, String goodsId, GoodsTypeEnum goodsType, Order goodsOrder) {
		super(OrderIdUtils.uuid());
		this.couponOrderId = buyerCoupon.getOrderId();
		this.userId = goodsOrder.getBuyer().id();
		this.goodsId = goodsId;
		this.goodsType = goodsType;
		this.goodsOrder = goodsOrder;
		this.level = buyerCoupon.getLevel();
		this.couponId = buyerCoupon.getCouponId();
	}
}
