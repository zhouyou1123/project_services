package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;

public interface BoughtService {

    public void validateDuplicateBuy(Buyer buyer, String goodsId, GoodsTypeEnum type);

}
