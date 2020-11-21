package com.sioeye.youle.run.order.context;


import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.context.codec.OrderStatusEnumCodec;
import com.sioeye.youle.run.order.context.codec.PayWayEnumCodec;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PersonalOrderResourceResponse {
    private String orderId;
    private String orderNo;
    private Date placeOrderDate;
    private String userId;
    private String openId;
    private Date paymentDate;
    @JSONField(serializeUsing = OrderStatusEnumCodec.class,deserializeUsing = OrderStatusEnumCodec.class)
    private OrderStatusEnum orderStatus;
    @JSONField(serializeUsing = PayWayEnumCodec.class,deserializeUsing = PayWayEnumCodec.class)
    private PayWayEnum payWay;
    private BigDecimal originalAmount;
    private BigDecimal actualAmount;
    private String parkId;
    private String parkName;
    private String currency;
    private List<PersonalOrderItemResourceResponse> orderItem;
    private PersonalUserCouponResponse userCoupon;
    private Integer orderType;
}
