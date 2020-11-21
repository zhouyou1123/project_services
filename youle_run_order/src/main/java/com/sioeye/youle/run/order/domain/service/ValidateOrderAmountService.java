package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.validate.ValidateFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderAmountService {

    private ValidateFactory validateFactory;

    public ValidateOrderAmountService(ValidateFactory validateFactory) {
        this.validateFactory = validateFactory;
    }

    public void validateAmount(OrderContext orderContext) {
        validateFactory.getValidateAmount(orderContext.getPromotionType()).validateAmount(orderContext::validateOriginalAmount, orderContext::validateActualAmount);
    }
}
