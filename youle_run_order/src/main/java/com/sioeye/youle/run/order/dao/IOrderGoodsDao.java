package com.sioeye.youle.run.order.dao;

import com.sioeye.youle.run.order.model.Order;
import com.sioeye.youle.run.order.model.OrderItem;
import com.sioeye.youle.run.order.model.OrderItemExtend;
import com.sioeye.youle.run.order.model.UserCoupon;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IOrderGoodsDao {

	/**
	 * 插入
	 *
	 * @param order
	 * @return int
	 */
	public int save(Order order);

	public int saveOrderItem(OrderItem orderItem);


	public int saveOrderItemExtend(OrderItemExtend orderItemExtend);

	/**
	 * 获取订单详情
	 *
	 * @param objectId
	 * @return Order
	 */
	public Order getOrderDetail(String objectId);

	/**
	 * 获取订单项详情
	 *
	 * @param orderId
	 * @return Order
	 */
	public List<OrderItem> getOrderItemDetail(String orderId);



	/**
	 * 获取订单项扩展详情
	 *
	 * @param orderId
	 * @return Order
	 */
	public List<OrderItemExtend> getOrderItemExtendDetail(String orderId);

	public UserCoupon getUserCoupon(@Param("userid") String userid, @Param("amusementparkid") String amusementparkid);

	public void saveUserCoupon(UserCoupon userCoupon);

	public void updateUserCoupon(UserCoupon userCoupon);

	public void updateOrderItem(OrderItem orderItem);

	public Order getLastOrderByUserPaid(@Param("userid") String userid,@Param("parkid") String parkid, @Param("type") int type);

	public Order getLastOrderByUserType(@Param("userid") String userid,@Param("parkid") String parkid, @Param("type") int type);

	/**
	 * 根据clipId和userId获取最新的order
	 *
	 * @param userId
	 * @param clipId
	 * @return Order
	 */
	public Order getOrderDetailByClipId(@Param("userId") String userId, @Param("clipId") String clipId);

	/**
	 * 查询支付成功的用户对应的clip
	 *
	 * @param userId
	 * @param clipId
	 * @return Order
	 */
	public Order getOrderPaySuccessByClipId(@Param("userId") String userId, @Param("clipId") String clipId);

	/**
	 * 查询照片订单
	 *
	 * @param userId
	 * @param photoId
	 * @return Order
	 */
	public Order getOrderPaySuccessByPhotoId(@Param("userId") String userId, @Param("photoId") String photoId);

	/**
	 *
	 * @param userId
	 * @param photoId
	 * @return Order
	 */
	public Order getOrderDetailByPhotoId(@Param("userId") String userId, @Param("photoId") String photoId);

	/**
	 * 获取订单列表
	 *
	 * @param map
	 * @return List<Order>
	 */
	public List<Order> getOrderList(Map<String, Object> map);

	/**
	 * 获取指定时间内用户全额支付的订单数
	 *
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public int getOrderFullPriceCount(@Param("usersId") String userId, @Param("amusementParkId") String amusementParkId,
                                      @Param("startDate") Date startDate, @Param("endDate") Date endDate);

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
                                           @Param("amusementParkId") String amusementParkId, @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate);

	/**
	 * 获取订单列表总数
	 *
	 * @param map
	 * @return int
	 */
	public int getOrderListCount(Map<String, Object> map);

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
}
