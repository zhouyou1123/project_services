package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.gateways.mq.OrderFilingListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FillingRabbitMQListenerConfig {
    @Autowired
    OrderFilingListener orderFilingListener;

    @Autowired
    ConnectionFactory connectionFactory;
    @Value("${order.filling.copy-request-queue}")
    private String orderFillingQueue;
    @Value("${order.filling.copy-response-queue}")
    private String orderFillingCallbackQueue;


    @Bean
    public Queue createOrderFillingCallbackQueue() {
        return new Queue(orderFillingCallbackQueue, true); // 队列持久
    }

    @Bean
    public SimpleMessageListenerContainer fillingGoodsResourceContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(createOrderFillingCallbackQueue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(10);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
        container.setMessageListener(orderFilingListener);
        return container;
    }
}
