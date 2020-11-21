package com.sioeye.youle.run.order.domain;

import java.util.Date;
import java.util.Set;

import com.sioeye.youle.run.order.domain.buyer.BuyerCoupon;
import com.sioeye.youle.run.order.domain.order.CouponOrder;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderDo;

public interface OrderRepository {
//    public String nextOrderId();
//    public String nextOrderItemId();
//
//    public int validateDuplicateBySuccess(String buyer,String goodsId);
//    public Order getOrderByNotPay(String buyer, String goodsId);
//
//
//    public Order getLastOrderByPaid(String buyer,String parkId, GoodsTypeEnum goodsType);
//    public Order getLastOrderByUser(String buyer,String parkId, GoodsTypeEnum goodsType);
//
//    public int getOrderCountByFullPrice(Buyer buyer, Park parkId, Date startDate, Date endDate);
//    public int getOrderCountByFreePrice(Buyer buyer, Park parkId, Date startDate, Date endDate);
//
//    public BuyerCoupon getBuyerCoupon(String buyer,String parkId);
//    public void saveBuyerCoupon(BuyerCoupon buyerCoupon);
//
//    public void saveOrder(Order order);
//
//    public Order getOrder(String orderId);
//
//    public void savePaidBack(Order order);


    /**
     * 获取成功购买的数量
     *
     * @param buyer
     *            买家id
     * @param goodsId
     *            商品id
     */
    public int getOrderCountByPaid(String buyer, String goodsId);

    /**
     * 获取用户、商品对应的最后一条支付的订单信息
     *
     * @param buyer
     * @param goodsId
     * @return
     */
    public Order getLastPaidOrderByGoodsId(String buyer, String goodsId,Integer goodsType);

    /**
     * 获取最后一条未支付订单
     *
     * @param buyer
     *            买家id
     * @param goodsId
     *            商品id
     * @return
     */
    public Order getLastOrderByGoodsId(String buyer, String goodsId,Integer goodsType);

    /**
     * 查询最后一条支付的订单信息
     *
     * @param buyer
     *            买家id
     * @param parkId
     *            游乐园id
     * @param goodsType
     *            商品类型
     * @return
     */
    public Order getLastPaidOrderByParkId(String buyer, String parkId, Integer goodsType);

    /**
     * 查询最后一条订单信息
     *
     * @param buyer
     *            买家id
     * @param parkId
     *            游乐园id
     * @param goodsType
     *            商品类型
     * @return
     */
    public Order getLastOrderByParkId(String buyer, String parkId, Integer goodsType);

    /**
     * 获取全价购买的商品数量
     *
     * @param buyer
     *            买家id
     * @param parkId
     *            游乐园id
     * @param goodsType
     *            商品类型
     * @param startDate
     *            开始日期
     * @param endDate
     *            结束日期
     * @return
     */
    public int getOrderCountByFullPrice(String buyer, String parkId, Integer goodsType, Date startDate,
                                        Date endDate);




    /**
     * 获取普通优惠购买的商品数量
     *
     * @param buyer
     *            买家id
     * @param parkId
     *            游乐园id
     * @param goodsType
     *            商品类型
     * @param startDate
     *            开始日期
     * @param endDate
     *            结束日期
     * @return
     */
    public int getOrderCountByFreePrice(String buyer, String parkId, Integer goodsType, Date startDate,
                                        Date endDate);


    /**
     * 获取赠送购买的商品数量
     * @param buyer
     * @param parkId
     * @param goodsType
     * @param startDate
     * @param endDate
     * @return
     */
    public int getOrderCountByPresent(String buyer, String parkId, Integer goodsType, Date startDate,
                                        Date endDate);

    public BuyerCoupon getBuyerCoupon(String buyer, String parkId);

    public BuyerCoupon getBuyerGameCoupon(String buyer, String goodsId);
    public BuyerCoupon getBuyerGameCouponByGameId(String buyerId, String parkId,String gameId);

    public void saveBuyerCoupon(BuyerCoupon buyerCoupon);

    public void saveOrder(Order order);

    
    public OrderDo getOrderById(String orderId);
    /**
     * 获取订单信息
     * @param orderId 订单id
     * @return
     */
    public Order getOrder(String orderId);

    /**
     * 获取订单信息
     * @param orderId 订单id
     * @param buyer 买家id
     * @return
     */
    public Order getOrderByUser(String orderId,String buyer);
    
    public OrderDo getOrderByUserId(String orderId,String userId);

    public void savePaidOrder(Order order);

    public void saveFilingOrder(Order order);

    public void saveShareActivityOrder(Order order);


    /**
     * 保存套餐购买订单
     * @param couponOrder
     * void
     */
    public void saveCouponOrder(CouponOrder couponOrder);


    /**
     * 获取用户已经购买的商品id列表
     * @param buyer
     * @param parkId
     * @param goodsType
     * @param startDate
     * @param endDate
     * @return
     */
    public Set<String> getGoodsIdsByUserPark(String buyer, String parkId, Integer goodsType, Date startDate, Date endDate);
}
