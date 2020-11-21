package com.sioeye.youle.run.order.gateways.repository.dao;

import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * OrderDao继承基类
 */
@Repository
public interface OrderDao extends MyBatisBaseDao<OrderDo, String> {
    public OrderDo getOrderByUser(@Param("objectid") String orderId, @Param("userid") String userId);
}