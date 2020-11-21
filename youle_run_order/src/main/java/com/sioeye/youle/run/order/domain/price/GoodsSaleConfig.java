package com.sioeye.youle.run.order.domain.price;

import lombok.Data;

@Data
public class GoodsSaleConfig {
    private String paymentDescription;
    private Boolean needValidateDuplicate = false;
    private Integer[][] validResource;
    private Integer[] validGoods;
}
