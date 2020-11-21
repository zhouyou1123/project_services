package com.sioeye.youle.run.order.service.intf;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.context.BoughtOrderResultResponse;
import com.sioeye.youle.run.order.context.DisplayResponse;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年6月1日
 *
 * @fileName IOrder.java
 *
 * @todo 订单基础接口
 */
public interface IOrderBase {

	/**
	 * 创建订单
	 * 
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public JSONObject placeOrder(Map<String, Object> map) throws CustomException, Exception;

	/**
	 * 查询下载地址
	 * 
	 * @param tokenUserId
	 * @param params
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             HighlightBoughtDto
	 */
	public JSONObject getOrderHighlightUrl(String tokenUserId, Map<String, Object> params)
			throws CustomException, Exception;

	/**
	 * 查询订单详情
	 * 
	 * @param orderId
	 * @param flag
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             public JSONObject getOrderDetail(Map<String, Object> map)
	 *             throws CustomException, Exception;
	 */

	/**
	 * 视频播放页面
	 * 
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 *             JSONObject
	 */
	public DisplayResponse display(Map<String, Object> map) throws CustomException, Exception;

	/**
	 * 
	 * @Description:根据用户id分页查询已购商品列表
	 * @Author GuoGongwei
	 * @Time: 2019年9月16日上午11:15:16
	 * @param map
	 * @return
	 * @throws CustomException
	 * @throws Exception
	 */
	public BoughtOrderResultResponse queryBoughtList(Map<String, Object> map) throws CustomException, Exception;

}
