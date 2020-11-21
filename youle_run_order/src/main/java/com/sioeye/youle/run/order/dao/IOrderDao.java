package com.sioeye.youle.run.order.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sioeye.youle.run.order.gateways.repository.dataobject.BoughtOrderDo;
import com.sioeye.youle.run.order.model.Order;

public interface IOrderDao {

	/**
	 * 插入
	 * 
	 * @param order
	 * @return int
	 */
	public int save(Order order);

	/**
	 * 获取订单详情
	 * 
	 * @param objectId
	 * @return Order
	 */
	public Order getOrderDetail(String objectId);

	/**
	 * 根据clipId和userId获取最新的order
	 * 
	 * @param userId
	 * @param clipId
	 * @return Order
	 */
	public Order getOrderDetailByClipId(@Param("userId") String userId, @Param("clipId") String clipId);

	/**
	 * 获取指定时间内用户全额支付的订单数
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public int getOrderFullPriceCount(@Param("usersId") String userId, @Param("amusementParkId") String amusementParkId,
			@Param("type") int type, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	/**
	 * 获取指定时间内用户折扣支付的订单数
	 * 
	 * @param userId
	 * @param amusementParkId
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public int getOrderPromotionPriceCount(@Param("usersId") String userId,
			@Param("amusementParkId") String amusementParkId, @Param("type") int type,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);


	/**
	 * 修改订单状态
	 * 
	 * @param order
	 * @return int
	 */
	public int update(Order order);

	/**
	 * 修改订单
	 * 
	 * @param order
	 * @return int
	 */
	public int updateOrder(Order order);

	/**
	 * 修改订单
	 * 
	 * @param order
	 * @return int
	 */
	public int updateOrderId(@Param("order") Order order, @Param("orderId") String orderId);

	/**
	 * 根据用户和集锦id查询订单
	 * 
	 * @param usersId
	 * @param highlightId
	 * @return List<Order>
	 */
	public Order getOrderByHighlightId(@Param("usersId") String usersId, @Param("highlightId") String highlightId,
			@Param("type") Integer type);

	/**
	 * 查询用户正在合成中的订单数
	 * 
	 * @param usersId
	 * @param type
	 * @param highlightStatus
	 * @return int
	 */
	public int getOrderHighlightMergingCount(@Param("usersId") String usersId, @Param("type") Integer type,
			@Param("highlightStatus") Integer highlightStatus);

	/**
	 * 
	 * @Description:根据用户id分页查询已购商品列表
	 * @Author GuoGongwei
	 * @Time: 2019年9月16日下午2:45:15
	 * @param map
	 * @return
	 */
	public List<BoughtOrderDo> queryBoughtList(Map<String, Object> map);

	/**
	 * 
	 * @Description:根据用户id查询已购商品总数
	 * @Author GuoGongwei
	 * @Time: 2019年9月16日下午5:26:07
	 * @param map
	 * @return
	 */
	public Integer queryBoughtTotal(Map<String, Object> map);

}
