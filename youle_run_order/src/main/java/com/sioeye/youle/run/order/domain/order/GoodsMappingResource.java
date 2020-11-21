package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.resource.ResourceContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoodsMappingResource implements GoodsContext, ResourceContext {
    private String goodsId;
    private Integer goodsType;
    private String goodsName;
    private Integer resourceType;
    private ResourceCategory resourceCategory;
    private Boolean needCalcAmount;

    @Override
    public String goodsId() {
        return goodsId;
    }

    @Override
    public Integer goodsType() {
        return goodsType;
    }

    @Override
    public String resourceId() {
        return goodsId;
    }

    @Override
    public ResourceCategory resourceCategory() {
        return resourceCategory;
    }

    @Override
    public Integer resourceType() {
        return resourceType;
    }

    public Boolean getNeedCalcAmount() {
        return needCalcAmount;
    }

    public String getGoodsName() {
        return goodsName;
    }
}
