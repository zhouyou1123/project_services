package com.sioeye.youle.run.order.context;


import com.sioeye.youle.run.order.domain.payment.Payment;
import com.sioeye.youle.run.order.domain.payment.PaymentResult;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateOrderResponse {
    private String orderId;
    private String openId;
    private BigDecimal actualAmount;
    private BigDecimal originalAmount;
    private Integer count;
    private String sign;
    private Integer payWay;
    private PaymentResult resultDate;
    private Date updateTime;
    @Deprecated
    private String activityId;
    private Date orderTime;
    private String appId;
    private String amusementParkId;
    private String prepayId;
    @Deprecated
    private String tradeType;
    private Integer status;
}
