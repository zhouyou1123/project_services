package com.sioeye.youle.run.order.gateways.repository.dao;

import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemDo;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemExtensionDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * OrderItemDao继承基类
 */
@Repository
public interface OrderItemDao extends MyBatisBaseDao<OrderItemDo, String> {
	/**
	 * 获取指定时间内用户全额支付的订单数
	 *
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public int getOrderFullPriceCount(@Param("userid") String userId, @Param("amusementparkid") String parkId,
                                      @Param("goodstype") Integer goodsType, @Param("startdate") Date startDate, @Param("enddate") Date endDate);

	/**
	 * 获取指定时间内用户折扣支付的订单数
	 *
	 * @param userId
	 * @param parkId
	 * @param startDate
	 * @param endDate
	 * @return int
	 */
	public int getOrderPromotionPriceCount(@Param("userid") String userId, @Param("amusementparkid") String parkId,
                                           @Param("goodstype") Integer goodsType, @Param("startdate") Date startDate, @Param("enddate") Date endDate);

	/**
	 * 获取指定时间内赠送的用户订单数
	 * @param userId
	 * @param parkId
	 * @param goodsType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getOrderPresentCount(@Param("userid") String userId, @Param("amusementparkid") String parkId,
										   @Param("goodstype") Integer goodsType, @Param("startdate") Date startDate, @Param("enddate") Date endDate);

	/**
	 * 根据用户id和商品id获取支付成功的记录数
	 *
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int getOrderCountByUserPaid(@Param("userid") String userId, @Param("goodsid") String goodsId);

	/**
	 * 根据用户id和商品id获取最后一条的订单
	 *
	 * @param userId
	 *            用户id
	 * @param goodsId
	 *            商品id
	 * @param goodsType 商品类型
	 * @return
	 */
	public OrderItemDo getLastOrderByGoodsId(@Param("userid") String userId, @Param("goodsid") String goodsId,@Param("goodstype") Integer goodsType);

	/**
	 * 根据用户id和商品id，获取用户最后支付的订单项信息
	 *
	 * @param userId
	 * @param goodsId
	 * @param goodsType 商品类型
	 * @return
	 */
	public OrderItemDo getLastPaidOrderByGoodsId(@Param("userid") String userId, @Param("goodsid") String goodsId,@Param("goodstype") Integer goodsType);

	public List<OrderItemDo> selectOrderItems(String orderId);

	/**
	 * 获取已支付的最后一条订单
	 *
	 * @param userId
	 *            用户id
	 * @param parkId
	 *            游乐园id
	 * @param goodsType
	 *            商品类型
	 * @return
	 */
	public OrderItemDo getLastPaidOrderByParkId(@Param("userid") String userId, @Param("amusementparkid") String parkId,
                                          @Param("goodstype") Integer goodsType);



	/**
	 *
	 * 获取用户的最后一条订单
	 *
	 * @param userId
	 *            用户id
	 * @param parkId
	 *            游乐园id
	 * @param goodsType
	 *            商品类型
	 * @return
	 */
	public OrderItemDo getLastOrderByParkId(@Param("userid") String userId, @Param("amusementparkid") String parkId,
                                          @Param("goodstype") Integer goodsType);

	/**
	 * 获取用户订单列表
	 * @param userId 用户id
	 * @param goodsType 商品类型
	 * @param limit 起始行数
	 * @param offset 偏移行数
	 * @return
	 */
	public List<OrderItemExtensionDo> getUserOrderList(@Param("userid") String userId, @Param("goodstype") Integer goodsType,
                                                       @Param("limit") Long limit, @Param("offset") Integer offset);


	/**
	 * 获取用户订单数量
	 * @param userId 用户id
	 * @param goodsType 商品类型
	 * @return
	 */
	public Long getUserOrderCount(@Param("userid") String userId, @Param("goodstype") Integer goodsType);


	/**
	 * 获取用户已购买的商品id
	 * @param userId
	 * @param parkId
	 * @param goodsType
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Set<String> getGoodsIdsByUserPark(@Param("userid") String userId, @Param("amusementparkid") String parkId,
											 @Param("goodstype") Integer goodsType, @Param("startdate") Date startDate, @Param("enddate") Date endDate);
}