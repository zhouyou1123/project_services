package com.sioeye.youle.run.order.context;

import java.util.List;

import lombok.Data;

@Data
public class PriceResponse {

	private Boolean stopped;
	private String timeZone;
	private String currency;
	private Boolean enabled;
	private List<Object> priceList;
}
