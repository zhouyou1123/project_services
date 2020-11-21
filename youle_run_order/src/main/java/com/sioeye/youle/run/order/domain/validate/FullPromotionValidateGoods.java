package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
public class FullPromotionValidateGoods implements ValidateGoodsPromotion {


	// 组合商品
	private Collection<Integer> compositeGoodsType = Arrays.asList(EnumType.PRINTPHOTO.getCode(),
			EnumType.PRINT1ADD1.getCode());


	public FullPromotionValidateGoods(){
    }

	@Override
	public BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext) {

		Optional<GoodsPrice> goodsPrice = parkGoodsPrice.getGoodsPrice(goodsContext.goodsType());
		if (!goodsPrice.isPresent()) {
			throw new CustomException(DomainErrorCodeEnum.GOODS_SOLD_OUT.getCode(),
					String.format(DomainErrorCodeEnum.GOODS_SOLD_OUT.getMessage(),
							"goodsType:" + goodsContext.goodsType().toString()));
		}
		//TODO 原价验证逻辑
//		if(orderTradeTypeProperties.checkNeedCalcAmount(tradeType,goodsContext.goodsType())){
//            return goodsPrice.get().getPrice();
//        }
        return goodsPrice.get().getPrice();

	}
}
