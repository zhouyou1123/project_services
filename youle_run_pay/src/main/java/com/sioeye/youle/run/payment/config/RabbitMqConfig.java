package com.sioeye.youle.run.payment.config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sioeye.youle.run.payment.service.receiver.H5PaypalPaymentReceiver;

@Configuration
public class RabbitMqConfig {

	@Value("${spring.rabbitmq.host}")
	String host;// rabbitmq 地址
	@Value("${spring.rabbitmq.port}")
	int port;// rabbitmq 地址
	@Value("${spring.rabbitmq.username}")
	String username;// rabbitmq 地址
	@Value("${spring.rabbitmq.password}")
	String password;// rabbitmq 地址
	@Value("${order.payment.h5.paypal.queue}")
	private String orderPaymentH5PaypalQueue;
	@Autowired
	H5PaypalPaymentReceiver h5PaypalPaymentReceiver;// 订单H5的paypal购买消费类

	public Connection mqConnection = null;

	public void createMqConnection() throws IOException, TimeoutException {
		// 创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		// 设置RabbitMQ相关信息
		factory.setHost(host);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setPort(port);
		// 创建一个新的连接
		mqConnection = factory.newConnection();
	}

	@Bean
	public Queue createH5PaypalPaymentQueue() {
		return new Queue(orderPaymentH5PaypalQueue, true); // 队列持久
	}

	@Bean
	public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(host + ":" + port);
		if (username != null && !"".equals(username)) {
			connectionFactory.setUsername(username);
			connectionFactory.setPassword(password);
		}
		connectionFactory.setPublisherConfirms(true); // 必须要设置
		return connectionFactory;
	}

	/*
	 * 
	 * @return SimpleMessageListenerContainer TODO 创建订单购买队列
	 */
	@Bean
	public SimpleMessageListenerContainer paymentMembershipContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(createH5PaypalPaymentQueue());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(h5PaypalPaymentReceiver);
		return container;
	}
}
