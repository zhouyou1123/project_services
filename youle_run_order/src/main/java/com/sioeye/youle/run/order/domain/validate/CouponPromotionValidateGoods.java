package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.goods.ResourceGoods;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

@Component
public class CouponPromotionValidateGoods implements ValidateGoodsPromotion {
	@Autowired
	private BoughtService boughtService;

	@Override
	public BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice,
			OrderContext orderContext, PromotionContext promotionContext) {

		// TODO 照片打印 ，用套餐密码打印 现在是HardCode，后续照片打印也包含在套餐范围中时需要调整
		if (orderContext.orderType().equals(GoodsTypeEnum.PRINT.getCode())) {
			return BigDecimal.ZERO;
		}
		String userId = orderContext.userId();
		// 首先查询全园套餐
		BuyerCoupon buyerCoupon = this.boughtService.getBuyerCoupon(userId, parkGoodsPrice.getParkId());
		if (buyerCoupon == null) {
			// 没有查询到全园套餐,继续查询项目套餐
			Order order = orderContext.toOrder();
			// 循环orderItem,看是否每一个商品都购买过项目套餐
			Iterator<OrderItem> iterator = order.iterator();
			while (iterator.hasNext()) {
				// 保存订单项
				OrderItem next = iterator.next();
				// 资源商品
				ResourceGoods resourceGoods = (ResourceGoods) next.getGoods();
				buyerCoupon = this.boughtService.getBuyerGameCouponByGameId(userId, parkGoodsPrice.getParkId(),
						resourceGoods.getGame().id());
				if (buyerCoupon == null) {
					throw new RuntimeException(
							String.format("user(%s) is not buy coupon (%s).", userId, parkGoodsPrice.getParkId()));
				}
				// 验证项目套餐是否有效
				this.validateCoupon(buyerCoupon, goodsContext, userId);
			}
		} else {
			// 有全园套餐,那么直接使用全园套餐,验证用户套餐是否有效
			this.validateCoupon(buyerCoupon, goodsContext, userId);
		}
		// TODO 套餐购买其他商品验证逻辑
		// 通过 orderContext.toOrder().getGame() 可以获取项目id；
		// 1、优先验证全园套餐，如果全园套餐存在，就使用全园套餐；
		// 2、不存在全园套餐，就检查是否存在项目套餐，如果存在项目套餐就使用项目套餐； 使用方法：
		// boughtService.getBuyerGameCouponByGameId
		// 3、以上条件都不成立，直接抛出异常；
		// 4、如果使用了套餐需要调用 orderContext.useCoupon(buyerCoupon);
		orderContext.useCoupon(buyerCoupon);
		// 用了套餐都是0元
		return BigDecimal.ZERO;
	}

	private void validateCoupon(BuyerCoupon buyerCoupon, GoodsContext goodsContext, String userId)
			throws RuntimeException {
		if (buyerCoupon.validatePeriodOverdue(new Date())) {
			throw new RuntimeException(
					String.format("user(%s) bought coupon(orderId:%s) is overdue.", userId, buyerCoupon.getOrderId()));
		}
		if (!buyerCoupon.validateCanBuyGoodsType(goodsContext.goodsType())) {
			throw new RuntimeException(String.format("user coupon(%s) can not buy goodsType(%s)",
					buyerCoupon.getCanBuyGoodsTypes(), goodsContext.goodsType()));
		}
	}
}
