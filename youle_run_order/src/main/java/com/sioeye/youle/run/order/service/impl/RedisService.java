package com.sioeye.youle.run.order.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumOrdersStatus;
import com.sioeye.youle.run.order.util.ConstUtil;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年8月23日
 *
 * @fileName RedisService.java
 *
 * @todo redis加锁操作
 */
@Component
public class RedisService {

	@Autowired
	private RedisLockRegistry redisLockRegistry;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Value("${order.photoprint.rediskey.timeout}")
	private int orderPhotoprintTimeout;
	@Value("${order.photoprint.rediskey.timeout}")
	private int orderStatusTimeout;
	@Value("${order.user.create.lock.timeout}")
	private int orderUserCreateLockTimeout;

	public boolean lock(String key) throws CustomException, Exception {
		Lock lock = redisLockRegistry.obtain(key);
		lock.lock();
		return true;
	}

	public boolean tryLock(String key, long secord) throws InterruptedException {
		Lock lock = redisLockRegistry.obtain(key);
		return lock.tryLock(secord, TimeUnit.SECONDS);
	}

	public boolean unLock(String key) {
		Lock lock = redisLockRegistry.obtain(key);
		lock.unlock();
		return true;
	}


	/**
	 * 订单下单状态
	 * 
	 * @param key
	 */
	public void placeOrderStatus(String key) {
		redisTemplate.opsForValue().set(ConstUtil.ORDER_STATUS_KEY + key, EnumOrdersStatus.UN_PAID.getCode().toString(),
				orderStatusTimeout, TimeUnit.SECONDS);
	}

	/**
	 * 订单支付状态
	 *
	 * @param key
	 *            void
	 */

	public void payOrderStatus(String key) {
		redisTemplate.opsForValue().set(ConstUtil.ORDER_STATUS_KEY + key,
				EnumOrdersStatus.PAY_SUCCESS.getCode().toString(), orderStatusTimeout, TimeUnit.SECONDS);
	}

	/**
	 * 获取订单状态
	 * @param key
	 * @return
	 * String
	 */
	public String getOrderStatus(String key) {
		return redisTemplate.opsForValue().get(ConstUtil.ORDER_STATUS_KEY + key);
	}


	public String getCreateOrderKey(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void setCreateOrderKey(String key) {
		redisTemplate.opsForValue().set(key, key, orderUserCreateLockTimeout, TimeUnit.SECONDS);
	}
}