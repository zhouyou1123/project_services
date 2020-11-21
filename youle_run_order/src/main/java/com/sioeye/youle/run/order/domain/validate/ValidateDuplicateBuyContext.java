package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.resource.ResourceContext;

public interface ValidateDuplicateBuyContext extends GoodsContext {
    String userId();
    String parkId();
    /**
     * 全局开关，判断整个订单是否需要验证重复购买
     * 如打印1+1，不需要验证重复购买
     * @return
     */
    boolean needValidate();
}
