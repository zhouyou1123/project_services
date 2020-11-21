package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.validate.*;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderDuplicateBuyService{


    private ValidateFactory validateFactory;


    public ValidateOrderDuplicateBuyService(ValidateFactory validateFactory){
        this.validateFactory = validateFactory;
    }

    public void validateDuplicateBuy(OrderContext orderContext){
        for (GoodsContext goodsContext:orderContext.goodsList()) {
            validateFactory.getValidateDuplicateBuy(goodsContext.goodsType()).validate(new ValidateDuplicateBuyContext() {
                @Override
                public String userId() {
                    return orderContext.userId();
                }

                @Override
                public String goodsId() {
                    return goodsContext.goodsId();
                }

                @Override
                public String parkId() {
                    return orderContext.parkId();
                }

                @Override
                public Integer goodsType() {
                    return goodsContext.goodsType();
                }

                @Override
                public boolean needValidate() {
                    return orderContext.needValidateDuplicate();
                }
            });
        }
    }

}
