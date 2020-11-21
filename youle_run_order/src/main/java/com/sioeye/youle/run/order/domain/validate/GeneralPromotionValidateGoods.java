package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.price.GoodsPriceConfiguration;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import com.sioeye.youle.run.order.domain.price.PromotionPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Component
public class GeneralPromotionValidateGoods implements ValidateGoodsPromotion {

	@Autowired
	private OrderRepository orderRepository;
	@Value("${order.promotion.time}")
	private int orderPromotionTime;
	@Value("${order.promotion.photo.time}")
	private int orderPromotionPhotoTime;

	@Override
	public BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext) {

		String userId = orderContext.userId();
		Optional<GoodsPriceConfiguration> goodsPriceConfig = parkGoodsPrice
				.getGoodsPriceConfig(goodsContext.goodsType());
		if (!goodsPriceConfig.isPresent()) {
			throw new CustomException(DomainErrorCodeEnum.GOODS_SOLD_OUT.getCode(),
					String.format(DomainErrorCodeEnum.GOODS_SOLD_OUT.getMessage(),
							"goodsType:" + goodsContext.goodsType().toString()));
		}
		if (goodsPriceConfig.get().getPromotionPrice() == null) {
			throw new RuntimeException("promotion price is null.");
		}
		PromotionPrice promotionPrice = goodsPriceConfig.get().getPromotionPrice();
		String timeZone = parkGoodsPrice.getTimeZone();
		String parkId = parkGoodsPrice.getParkId();
		// 优惠活动过期，直接返回原始价格
		if (!promotionPrice.validateInPromotionPeriod(timeZone)) {
			return promotionPrice.getOriginalPrice();
		}
		// 1.获取本地当天的开始日期和结束日期
		Date localStartDate = null;
		Date localEndDate = null;
		if (goodsContext.goodsType() == GoodsTypeEnum.PHOTO.getCode()) {
			// 如果是照片，那么查询三天内的订单数量
			localStartDate = promotionPrice.calcLocalPeriodStartDate(timeZone,orderPromotionPhotoTime);
			localEndDate = promotionPrice.calcLocalPeriodEndDate(timeZone,orderPromotionPhotoTime);
		} else {
			localStartDate = promotionPrice.calcLocalPeriodStartDate(timeZone,orderPromotionTime);
			localEndDate = promotionPrice.calcLocalPeriodEndDate(timeZone,orderPromotionTime);
		}
		int orderCountByFreePrice = orderRepository.getOrderCountByFreePrice(userId, parkId, goodsContext.goodsType(),
				localStartDate, localEndDate);
		int orderCountByFullPrice = orderRepository.getOrderCountByFullPrice(userId, parkId, goodsContext.goodsType(),
				localStartDate, localEndDate);

		if (!promotionPrice.validateUsePromotion(orderCountByFullPrice, orderCountByFreePrice, timeZone)) {
			throw new CustomException(EnumHandle.NOT_SUPPORT_PARKDISCOUNT);
		}
		return promotionPrice.getPromotionPrice();
	}
}
