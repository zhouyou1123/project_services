package com.sioeye.youle.run.pay.yeepay;

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
 * @date 2019年3月27日
 *
 * @fileName YeePayApp.java 
 *
 * @todo 易宝支付
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class YeePayApp {
	public static void main(String[] args) {
		SpringApplication.run(YeePayApp.class, args);
		System.out.println("yeepay server start successfully .");
	}
}