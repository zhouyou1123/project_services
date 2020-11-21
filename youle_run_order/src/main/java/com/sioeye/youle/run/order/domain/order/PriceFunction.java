package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;

import java.math.BigDecimal;

@FunctionalInterface
public interface PriceFunction {
    BigDecimal apply(String parkId, String gameId, Integer goodsType);
}
