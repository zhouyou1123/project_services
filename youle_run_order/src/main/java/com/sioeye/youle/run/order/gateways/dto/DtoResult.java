package com.sioeye.youle.run.order.gateways.dto;

import lombok.Data;

@Data
public class DtoResult<T> {
    private Boolean success;
    private String code;
    private String message;
    private T value;
}
