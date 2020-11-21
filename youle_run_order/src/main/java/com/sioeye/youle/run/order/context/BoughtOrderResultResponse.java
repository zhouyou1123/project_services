package com.sioeye.youle.run.order.context;

import java.util.List;

import lombok.Data;

@Data
public class BoughtOrderResultResponse {

	private List<BoughtOrderResponse> list;
	private int total;
}
