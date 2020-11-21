package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import com.sioeye.youle.run.order.domain.price.ShareActivityPrice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ShareActivityPromotionValidateGoods implements ValidateGoodsPromotion {
	@Override
	public BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext) {

		Optional<ShareActivityPrice> shareActivityPrice = parkGoodsPrice.getShareActivityPrice(goodsContext.goodsType(),promotionContext.getPromotionId());

		ShareActivityPrice price = shareActivityPrice.orElseThrow(()->new CustomException(EnumHandle.SHARE_PRICE_NOT_SET));
		if (!price.canUseShareActivity()) {
			throw new CustomException(EnumHandle.NOT_SUPPORT_SHARE);
		} else {
			return price.getSharePrice();
		}
	}
}
