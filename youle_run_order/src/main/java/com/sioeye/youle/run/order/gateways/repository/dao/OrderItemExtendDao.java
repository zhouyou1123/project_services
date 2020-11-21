package com.sioeye.youle.run.order.gateways.repository.dao;

import com.sioeye.youle.run.order.gateways.repository.dataobject.OrderItemExtendDo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItemExtendDao继承基类
 */
@Repository
public interface OrderItemExtendDao extends MyBatisBaseDao<OrderItemExtendDo, String> {
    public List<OrderItemExtendDo> selectOrderItemExtends(String orderId);
}