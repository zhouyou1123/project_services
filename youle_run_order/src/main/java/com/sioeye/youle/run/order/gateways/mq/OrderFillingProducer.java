package com.sioeye.youle.run.order.gateways.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Slf4j
@Component
public class OrderFillingProducer {
    @Value("${order.filling.copy-request-queue}")
    private String orderFillingQueue;
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendOrderFillingMessage(String context){

        log.info(context);
        Message message = new Message(context.getBytes(Charset.forName("utf-8")),new MessageProperties());
        amqpTemplate.send(orderFillingQueue,message);
    }
}
