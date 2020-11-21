package com.sioeye.youle.run.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 
 * @author zhouyou 
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月5日
 *
 * @fileName PaymentApp.java 
 *
 * @todo 支付服务
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class PaymentApp {
	public static void main(String[] args) {
		SpringApplication.run(PaymentApp.class, args);
		System.out.println("payment server start successfully .");
	}
}