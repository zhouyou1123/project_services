package com.sioeye.youle.run.order.domain.payment;

import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.domain.buyer.Buyer;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class PaymentResult extends ValueObject {
    private String timeStamp;
    @JSONField(name = "package")
    private String packageString;
    private String paySign;
    private String appId;
    private String signType;
    private String nonceStr;
    @JSONField(serialize = false,deserialize = false)
    private PaymentId paymentId;
    @JSONField(serialize = false,deserialize = false)
    private Buyer buyer;
    @JSONField(serialize = false,deserialize = false)
    private String prePaymentId;
    @JSONField(serialize = false,deserialize = false)
    private String tradeType;
    @JSONField(serialize = false,deserialize = false)
    private Integer payWay;
}
