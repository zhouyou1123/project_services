package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;

import java.math.BigDecimal;

@FunctionalInterface
public interface ValidateCalculateGoodsPromotionFunction {
    BigDecimal apply(PromotionContext promotionContext, GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext);
}
