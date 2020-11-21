package com.sioeye.youle.run.order.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.context.DisplayRequest;
import com.sioeye.youle.run.order.context.OrderResponse;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.domain.order.OrderItem;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import com.sioeye.youle.run.order.util.ConstUtil;

@Component
public class DisplayOrderService {

	@Autowired
	private BoughtService boughtService;
	@Autowired
	private AdminParkService adminParkService;

	public Optional<Order> getOrderGoodsByUser(DisplayRequest displayRequest) {
		if (displayRequest.getNeedValidateDuplicate()) {
			return boughtService.checkIsGoods(displayRequest.getUserId(), displayRequest.getGoodsId(),
					displayRequest.getGoodsType());
		}
		return null;
	}

	public void setValidateDuplicateAndGoodsType(DisplayRequest displayRequest) {
		ParkGoodsPrice parkGoodsPrice = adminParkService.getParkGoodsPrice(displayRequest.getParkId(), null);
		Optional.ofNullable(parkGoodsPrice).orElseThrow(() -> new CustomException(EnumHandle.GET_PARK_PRICE_ERROR));
		boolean needValidateDuplicate = true;
		if (displayRequest.getGoodsType() != null) {
			// 有传goodsType
			needValidateDuplicate = parkGoodsPrice.getGoodsPrice(displayRequest.getGoodsType())
					.map(goodsPrice -> goodsPrice.getSaleConfig())
					.map(saleConfig -> saleConfig.getNeedValidateDuplicate())
					.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.PRICE_INCORRECT));
		} else {
			Optional<GoodsPrice> oGoodsPrice = parkGoodsPrice
					.getGoodsPriceByResourceType(displayRequest.getResourceType());
			Integer goodsType = oGoodsPrice.map(goodsPrice -> goodsPrice.getType())
					.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.PRICE_INCORRECT));
			displayRequest.setGoodsType(goodsType);
			needValidateDuplicate = oGoodsPrice.map(goodsPrice -> goodsPrice.getSaleConfig())
					.map(saleConfig -> saleConfig.getNeedValidateDuplicate())
					.orElseThrow(() -> CustomException.build(DomainErrorCodeEnum.PRICE_INCORRECT));
		}
		displayRequest.setNeedValidateDuplicate(needValidateDuplicate);
	}
}