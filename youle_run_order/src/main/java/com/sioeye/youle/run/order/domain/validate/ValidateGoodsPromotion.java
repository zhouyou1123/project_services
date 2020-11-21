package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public interface ValidateGoodsPromotion {
    default BigDecimal calculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext){
        if (goodsContext == null){
            throw new RuntimeException("goodsContext is null.");
        }
        if (parkGoodsPrice == null){
            throw new RuntimeException("parkGoodsPrice is null.");
        }
        if (orderContext == null){
            throw new RuntimeException("orderContext is null.");
        }
        if (promotionContext == null){
            throw new RuntimeException("promotionContext is null.");
        }
        return doCalculatePrice(goodsContext,parkGoodsPrice,orderContext,promotionContext);
    }
    BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext);
}
