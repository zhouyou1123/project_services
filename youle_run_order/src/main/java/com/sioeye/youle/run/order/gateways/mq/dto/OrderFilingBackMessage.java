package com.sioeye.youle.run.order.gateways.mq.dto;

import lombok.Data;

@Data
public class OrderFilingBackMessage {
    private String orderId;
    private Boolean success;

}
