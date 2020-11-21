package com.sioeye.youle.run.order.gateways.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class OrderFilingMessage {
    private String orderId;
    private String callbackQueue;
    private List<Map<String,Object>> tasks;

}
