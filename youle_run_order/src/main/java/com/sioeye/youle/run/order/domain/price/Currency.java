package com.sioeye.youle.run.order.domain.price;

import org.springframework.util.StringUtils;

public class Currency {
    private String currency;
    public Currency(String currency){
        if (!StringUtils.hasText(currency)){
            throw  new RuntimeException("currency is not null.");
        }
        this.currency = currency;
    }
    @Override
    public String toString(){
        return currency;
    }

    public static Currency buildDefault(){
        return new Currency("CNY");
    }
}
