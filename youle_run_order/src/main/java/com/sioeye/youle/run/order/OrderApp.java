package com.sioeye.youle.run.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import com.sioeye.youle.run.order.config.CustomException;

/**
 * 
 * @author zhouyou 
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月4日
 *
 * @fileName OrderApp.java 
 *
 * @todo order server
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class OrderApp {
	public static void main(String[] args) {
		SpringApplication.run(OrderApp.class, args);
		System.out.println("order server start successfully .");
	}
}