package com.sioeye.youle.run.order.context;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class OrderResponse {

	private String orderId;
	private Integer orderType;
	private Date orderTime;
	private Date paymentTime;
	private Integer status;
	private String shareUrl;
	private BigDecimal originalAmount;
	private BigDecimal actualAmount;
}