package com.sioeye.youle.run.order.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.sioeye.youle.run.order.gateways.mq.OrderFilingListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.sioeye.youle.run.order.service.receiver.OrderPaymentReceiver;
import com.sioeye.youle.run.order.service.receiver.WeishiShareReceiver;
import com.sioeye.youle.run.order.service.receiver.WeishiShareTtlConsumerReceiver;

@Slf4j
@Configuration
public class RabbitMQConfig {

	@Value("${spring.rabbitmq.host}")
	String host;// rabbitmq 地址
	@Value("${spring.rabbitmq.port}")
	int port;// rabbitmq 地址
	@Value("${spring.rabbitmq.username}")
	String username;// rabbitmq 地址
	@Value("${spring.rabbitmq.password}")
	String password;// rabbitmq 地址
	@Value("${order.payment.queue}")
	private String orderPaymentQueue;
	@Value("${order.payment.fail.queue}")
	private String orderPaymentFailQueue;
	@Value("${weishi.share.queue}")
	private String weishiShareQueue;
	@Value("${weishi.share.ttl.time}")
	private int weishiShareTtlTime;
	@Value("${weishi.share.ttl.exchange}")
	private String weishiShareTtlExchange;
	@Value("${weishi.share.ttl.queue}")
	private String weishiShareTtlQueue;
	@Value("${weishi.share.ttl.fail.queue}")
	private String weishiShareTtlFailQueue;
	@Value("${weishi.share.ttl.consumer.queue}")
	private String weishiShareTtlConsumerQueue;

	@Value("${order.event.exchange}")
	private String orderEventNotificationExchange;

	@Autowired
	OrderPaymentReceiver orderPaymentReceiver;// 订单购买消费类
	@Autowired
	WeishiShareReceiver weishiShareReceiver;// 分享微视过期查询微视分享地址消费者
	@Autowired
	WeishiShareTtlConsumerReceiver weishiShareTtlConsumerReceiver;


	public Connection mqConnection = null;

	public synchronized Connection createMqConnection() throws IOException, TimeoutException {
		if (mqConnection == null) {
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
		return mqConnection;
	}

	/*
	 * 
	 * @return ConnectionFactory TODO 连接rabbitmq服务器
	 */
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

	@Bean
	public String createOrderEventExchange(){
		try {
			com.rabbitmq.client.Channel channel = this.connectionFactory().createConnection().createChannel(false);
			// 创建exchange
			AMQP.Exchange.DeclareOk declareOk = channel.exchangeDeclare(orderEventNotificationExchange, BuiltinExchangeType.TOPIC, true, false, null);
			channel.close();
			return declareOk.toString();
		} catch (IOException e) {
			log.error("createOrderEventExchange is error:"+e.getMessage());
		} catch (TimeoutException e) {
			log.error("createOrderEventExchange is error:"+e.getMessage());
		}
		return null;
	}

	@Bean
	public String createWeishiShareTtlQueue() {
		try {
			com.rabbitmq.client.Channel channel = this.connectionFactory().createConnection().createChannel(false);
			// 创建exchange
			channel.exchangeDeclare(weishiShareTtlExchange, BuiltinExchangeType.TOPIC, true, false, null);
			Map<String, Object> argss = new HashMap<String, Object>();
			argss.put("x-message-ttl", weishiShareTtlTime);
			argss.put("x-dead-letter-exchange", weishiShareTtlExchange);
			argss.put("x-dead-letter-routing-key", weishiShareTtlExchange);
			DeclareOk declareOk = channel.queueDeclare(weishiShareTtlQueue, true, false, false, argss);
			channel.close();
			return declareOk.getQueue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean
	public String createWeishiShareTtlConsumerQueue() {
		try {
			com.rabbitmq.client.Channel channel = this.createMqConnection().createChannel();
			// 创建队列
			DeclareOk declareOk = channel.queueDeclare(weishiShareTtlConsumerQueue, true, false, false, null);
			// 绑定队列到交换机
			channel.queueBind(weishiShareTtlConsumerQueue, weishiShareTtlExchange, weishiShareTtlExchange);
			channel.close();
			return declareOk.getQueue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean
	public Queue createPaymentQueue() {
		return new Queue(orderPaymentQueue, true); // 队列持久
	}

	@Bean
	public Queue createPaymentFailQueue() {
		return new Queue(orderPaymentFailQueue, true); // 处理支付失败队列
	}

	@Bean
	public Queue createWeishiShareTtlFailQueue() {
		return new Queue(weishiShareTtlFailQueue, true); // 处理分享微视失败queue
	}




	/*
	 * 
	 * @return SimpleMessageListenerContainer TODO 创建订单购买队列
	 */
	@Bean
	public SimpleMessageListenerContainer paymentMembershipContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(createPaymentQueue());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(orderPaymentReceiver);
		return container;
	}

	/*
	 * 微视分享消费者
	 */
	@Bean
	public SimpleMessageListenerContainer weishiShareContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueueNames(weishiShareQueue);
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(weishiShareReceiver);
		return container;
	}

	/*
	 * TODO 查询微视分享url超时监听
	 */
	@Bean
	public SimpleMessageListenerContainer weishiShareTtlContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueueNames(createWeishiShareTtlConsumerQueue());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(weishiShareTtlConsumerReceiver);
		return container;
	}





}
