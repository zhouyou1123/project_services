package com.sioeye.youle.run.order.gateways.mq.dto;


import lombok.Data;

import java.util.Date;

@Data
public class OrderPaidMessage {
    private String orderId;
    private String prePayId;
    private String userId;
    private String openId;
    private Date paidDate = new Date();


    public Boolean isFreeOrder(){
        return "disney_free_payment".equals(prePayId);
    }

}
