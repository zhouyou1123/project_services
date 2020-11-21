package com.sioeye.youle.run.order.context;

import com.sioeye.youle.run.order.domain.resource.ResourceCategory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DisplayRequest {

	private String userId;
	private String parkId;
	private Integer resourceType;
	private ResourceCategory resourceCategory;
	private String goodsId;
	private Integer goodsType;
	private Boolean needValidateDuplicate = true;
}