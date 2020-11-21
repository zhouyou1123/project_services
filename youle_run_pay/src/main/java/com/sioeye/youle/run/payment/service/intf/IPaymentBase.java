package com.sioeye.youle.run.payment.service.intf;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.payment.config.CustomException;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName IPaymentBase.java
 *
 * @todo 支付基础接口
 */
public interface IPaymentBase {

	/**
	 * 预下单
	 * 
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public JSONObject prePayment(Map<String, Object> map) throws CustomException, Exception;

	/**
	 * 查询订单详情
	 * 
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public JSONObject getPaymentDetail(Map<String, Object> map) throws CustomException, Exception;

	/**
	 * 支付回调接口
	 * 
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public JSONObject payBack(Map<String, String> map) throws CustomException, Exception;
}
