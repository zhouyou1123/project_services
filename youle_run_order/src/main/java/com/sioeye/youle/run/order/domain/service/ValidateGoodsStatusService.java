package com.sioeye.youle.run.order.domain.service;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.GoodsMappingResource;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.validate.ValidateFactory;
import com.sioeye.youle.run.order.domain.validate.ValidateGoodsStatusContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ValidateGoodsStatusService {

	private int corePoolSize = 10;
	private int maxPoolSize = 50;
	private int poolQueueSize = 100;

	private Executor executor = null;

	private ValidateFactory validateFactory = null;

	public ValidateGoodsStatusService(ValidateFactory validateFactory) {
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(poolQueueSize), new ThreadFactory() {
					private int count = 1;

					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("ValidatePool-" + count++);
						t.setDaemon(true);
						return t;
					}
				});
		this.validateFactory = validateFactory;
	}

	public void validateStatus(OrderContext orderContext) {
		long startTime = System.currentTimeMillis();
		List<CompletableFuture<Class<Void>>> completableFutureList = new ArrayList<>(orderContext.goodsList().size());
		for (GoodsMappingResource goodsContext : orderContext.goodsMappingResourcesList()) {
			CompletableFuture<Class<Void>> classCompletableFuture = CompletableFuture.supplyAsync(() -> {
				return validateFactory.getValidateGoodsStatus(goodsContext.goodsType());
			}, executor).thenApply(status -> {
				status.validate(new ValidateGoodsStatusContext() {
					@Override
					public String resourceId() {
						return goodsContext.goodsId();
					}

					@Override
					public ResourceCategory resourceCategory() {
						return goodsContext.resourceCategory();
					}

					@Override
					public Integer resourceType() {
						return goodsContext.resourceType();
					}

					@Override
					public String goodsId() {
						return goodsContext.goodsId();
					}

					@Override
					public Integer goodsType() {
						return goodsContext.goodsType();
					}

					@Override
					public String parkId() {
						return orderContext.parkId();
					}

					@Override
					public String name() {
						return goodsContext.getGoodsName();
					}

				}, orderContext::checkGoodsIsOnShelf, orderContext::addGoods);
				return Void.TYPE;
			});
			completableFutureList.add(classCompletableFuture);
		}
		completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

		log.info("validateStatus sum time:" + (System.currentTimeMillis() - startTime));
	}

}
