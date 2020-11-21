package com.sioeye.youle.run.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
public class RedisLockRegistryConfig {

	@Value("${order.user.create.timeout}")
	private int orderUserCreateTimeout;

	@Bean
	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) { // 指定redis
																								// 服务器上锁前缀，指定锁的过期时间，
		return new RedisLockRegistry(redisConnectionFactory, "youle-mvc", orderUserCreateTimeout);
	}
}