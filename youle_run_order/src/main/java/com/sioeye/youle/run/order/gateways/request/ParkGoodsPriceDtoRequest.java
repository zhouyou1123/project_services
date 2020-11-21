package com.sioeye.youle.run.order.gateways.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkGoodsPriceDtoRequest {
    private String parkId;
    private String gameId;
    private Integer goodsType;
}
