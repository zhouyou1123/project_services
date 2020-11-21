package com.sioeye.youle.run.order.domain.order;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import com.sioeye.youle.run.order.domain.price.Currency;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderAmount extends ValueObject {
    private BigDecimal originalAmount;
    private BigDecimal actualAmount;
    private Currency currency;

    public OrderAmount(BigDecimal originalAmount,BigDecimal actualAmount,Currency currency){
        setOriginalAmount(originalAmount);
        setActualAmount(actualAmount);
        setCurrency(currency);
    }

    public BigDecimal getPromotionAmount(){
        return originalAmount.subtract(actualAmount);
    }

    /**
     * 验证原始价格
     */
    public void validateOriginalAmount(BigDecimal originalAmount){
        if (this.originalAmount.compareTo(originalAmount) != 0 ){
            throw new CustomException(DomainErrorCodeEnum.PLACE_ORDER_ORIGINALAMOUNT_PARAM_INCORRECT.getCode(), DomainErrorCodeEnum.PLACE_ORDER_ORIGINALAMOUNT_PARAM_INCORRECT.getMessage());
        }

    }
    public void validateActualAmount(BigDecimal actualAmount){
        if (this.actualAmount.compareTo(actualAmount) != 0){
            throw new CustomException(DomainErrorCodeEnum.PLACE_ORDER_ACTUALAMOUNT_PARAM_INCORRECT.getCode(), DomainErrorCodeEnum.PLACE_ORDER_ACTUALAMOUNT_PARAM_INCORRECT.getMessage());
        }
    }
    private void setOriginalAmount(BigDecimal originalAmount){
        assertArgumentNotNull(originalAmount,"the original amout of order is not null.");
        if (originalAmount.compareTo(BigDecimal.ZERO) < 0 ){
            throw new RuntimeException("the original amount of order is equal to and greater than 1.");
        }
        this.originalAmount = originalAmount;
    }
    private void setActualAmount(BigDecimal actualAmount){
        assertArgumentNotNull(actualAmount,"the actual amount of order is not null.");
        if (actualAmount.compareTo(BigDecimal.ZERO) < 0 ){
            throw new RuntimeException("the actual amount of order is equal to and greater than 1.");
        }
        this.actualAmount = actualAmount;
    }
    private void setCurrency(Currency currency){
        assertArgumentNotNull(currency,"the currency of amount is not null.");
        this.currency = currency;
    }

}
