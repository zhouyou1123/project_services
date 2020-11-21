package com.sioeye.youle.run.order.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sioeye.youle.run.order.context.CreateOrderRequest;
import com.sioeye.youle.run.order.context.CreateOrderResponse;
import com.sioeye.youle.run.order.context.OrderStatusResponse;
import com.sioeye.youle.run.order.context.PersonalOrderResourceResponse;
import com.sioeye.youle.run.order.context.PersonalPresent;
import com.sioeye.youle.run.order.domain.order.Order;
import com.sioeye.youle.run.order.service.intf.IOrder;

public interface IGoodsOrderAppService extends IOrder {


	public OrderStatusResponse getOrderStatus(Map<String, Object> params);
	
    /**
     * 下单
     * @param orderRequest
     * @return
     */
    public CreateOrderResponse placeOrder(CreateOrderRequest orderRequest);


    /**
     * 订单支付成功回调
     * @param orderId
     */
    public void paidBack(String orderId);

    /**
     * 归档（复制）文件成功回调
     *
     * @param orderId
     */
    public void filingBack(String orderId);

    public Order getOrder(String orderId);


    /**
     * 获取订单的商品资源（个人中心）
     * @param orderId
     * @param userId
     * @return
     */
    public PersonalOrderResourceResponse getOrderResourceByUserId(String orderId, String userId);

    /**
     * 检测用户是否购买指定商品(默认是小视频)
     * @param userId 用户id
     * @param goodsId 商品id
     * @return
     */
    @Deprecated
    public boolean checkIsBoughtGoodsByUser(String userId,String goodsId);

    /**
     * 检测用户是否购买指定商品
     * @param userId
     * @param goodsId
     * @param goodsType
     * @return
     */
    public boolean checkIsBoughtGoodsByUser(String userId, String goodsId, Integer goodsType);

    /**
     * 获取用户已购买过商品的信息(默认是小视频)
     * @param userId
     * @param goodsId
     * @return
     * Optional<Order>
     */
    @Deprecated
    public Optional<Order> getPaidBoughtGoodsByUser(String userId,String goodsId);

    /**
     * 获取用户已购买过商品的信息
     * @param userId
     * @param goodsId
     * @param goodsType
     * @return
     */
    public Optional<Order> getPaidBoughtGoodsByUser(String userId,String goodsId,Integer goodsType);

    /**
     * 获取用户下单的商品(支付和未支付的)
     * @param userId
     * @param goodsId
     * @param goodsType
     * @return
     * Optional<Order>
     */
    public Optional<Order> getOrderGoodsByUser(String userId,String goodsId,Integer goodsType);


    /**
     * 获取个人的赠送权益
     * @param userId
     * @param parkId
     * @return
     */
    public List<PersonalPresent> getPersonalPresentRight(String userId, String parkId);
}
