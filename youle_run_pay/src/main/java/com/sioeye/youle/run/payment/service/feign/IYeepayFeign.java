package com.sioeye.youle.run.payment.service.feign;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName IPaymentFeign.java
 *
 * @todo 易宝支付feign接口
 */
@FeignClient(name = "youle-run-pay-yeepay")
public interface IYeepayFeign {

	/**
	 * 预支付
	 * 
	 * @param orders
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/yeepay/pre_yeepay")
	public String preYeepay(@RequestBody Map<String, Object> map);

	/**
	 * 查询支付详情
	 * 
	 * @param orders
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/yeepay/get_yeepay_order_detail")
	public String getYeepayDetail(@RequestBody Map<String, Object> map);

}