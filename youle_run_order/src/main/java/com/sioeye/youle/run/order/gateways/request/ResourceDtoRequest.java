package com.sioeye.youle.run.order.gateways.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDtoRequest {
    private String resourceId;
    private Integer resourceType;
    private Integer resourceCategory;
}
