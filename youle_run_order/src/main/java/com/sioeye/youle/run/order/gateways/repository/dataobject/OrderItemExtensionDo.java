package com.sioeye.youle.run.order.gateways.repository.dataobject;

import lombok.Data;

import java.util.Date;

/**
 * orderitem
 * @author
 */
@Data
public class OrderItemExtensionDo extends OrderItemDo {
    private String orderno;
    private String timezone;
    private Date paymenttime;
}