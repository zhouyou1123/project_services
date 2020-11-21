package com.sioeye.youle.run.pay.yeepay.service.intf;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.pay.yeepay.config.CustomException;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年3月27日
 *
 * @fileName IYeepay.java
 *
 * @todo 易宝支付接口
 */
public interface IYeepay {

	/**
	 * 易宝支付预下单
	 * 
	 * @param params
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             JSONObject
	 */
	public JSONObject preYeepay(Map<String, Object> params) throws CustomException, Exception;

	/**
	 * 查询订单详情
	 * 
	 * @param orderId
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             JSONObject
	 */
	public JSONObject getYeepayOrderDetail(Map<String, Object> params) throws CustomException, Exception;
}
