package com.sioeye.youle.run.order.service.feign;

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
 * @todo 支付feign接口
 */
@FeignClient(name = "youle-run-payment")
public interface IPaymentFeign {

	/**
	 * 预支付
	 * 
	 * @param orders
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/payment/pre_payment")
	public String prePayment(@RequestBody Map<String, Object> map);

	/**
	 * 查询支付详情
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/payment/get_payment_detail")
	public String getPaymentDetail(@RequestBody Map<String, Object> map);
}
