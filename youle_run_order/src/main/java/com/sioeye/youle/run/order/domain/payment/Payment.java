package com.sioeye.youle.run.order.domain.payment;

import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Payment extends ValueObject {
    private PayWayEnum payWay;
    private String tradeType;
    private Buyer buyer;
    private String amusementParkId;
    private String parkName;
    private String orderId;
    private String goodsName;
    private String goodsDesc;
    private String spbillCreateIp;
    private BigDecimal actualAmount;
}
