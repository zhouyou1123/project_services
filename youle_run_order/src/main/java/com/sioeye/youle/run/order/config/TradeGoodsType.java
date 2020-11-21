package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import lombok.Data;

@Data
public class TradeGoodsType {
    private GoodsTypeEnum goodsType;
    private Boolean needCalcAmount;
}
