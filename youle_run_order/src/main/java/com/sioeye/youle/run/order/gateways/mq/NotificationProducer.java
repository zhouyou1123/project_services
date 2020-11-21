package com.sioeye.youle.run.order.gateways.mq;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.domain.order.event.PlacedOrderEventMessage;
import com.sioeye.youle.run.order.interfaces.NotificationService;
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
public class NotificationProducer implements NotificationService {
    @Value("${order.event.exchange}")
    private String orderEventNotificationExchange;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendNotification(PlacedOrderEventMessage notificationMessage) {
        String context = JSONObject.toJSONString(notificationMessage);
        log.info(context);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("contentType","text/plain");
        properties.setHeader("originalContentType","application/json;charset=UTF-8");
        Message message = new Message(context.getBytes(Charset.forName("utf-8")),properties);
        amqpTemplate.send(orderEventNotificationExchange,"#",message);
    }
}
