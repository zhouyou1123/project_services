package com.sioeye.youle.run.order.service.intf;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;

/**
 * 
 * @author zhouyou 
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName IOrderBase.java 
 *
 * @todo 订单基类
 */
public interface IOrder {

	/**
	 * 创建订单
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public JSONObject placeOrder(Map<String, Object> params) throws CustomException, Exception;
}
