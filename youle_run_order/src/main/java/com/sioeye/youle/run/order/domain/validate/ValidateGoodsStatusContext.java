package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.resource.ResourceContext;

public interface ValidateGoodsStatusContext extends GoodsContext, ResourceContext {
    String parkId();
    String name();
}
