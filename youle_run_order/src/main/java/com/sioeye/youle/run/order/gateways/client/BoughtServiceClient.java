package com.sioeye.youle.run.order.gateways.client;

import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.interfaces.BoughtService;

public class BoughtServiceClient implements BoughtService {

    @Override
    public void validateDuplicateBuy(Buyer buyer, String goodsId, GoodsTypeEnum type) {

    }
}
