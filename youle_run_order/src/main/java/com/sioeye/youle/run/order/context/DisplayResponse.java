package com.sioeye.youle.run.order.context;

import lombok.Data;

@Data
public class DisplayResponse {

	private GoodsResponse goods;
	private OrderResponse order;
	private PriceResponse price;
}
