package com.sioeye.youle.run.order.gateways.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface OrderFilingTopic {
//    String INPUT = "order-filing-input";
//    @Input(INPUT)
    SubscribableChannel input();

    String OUTPUT = "order-filing-output";
    @Output(OUTPUT)
    MessageChannel output();
}
