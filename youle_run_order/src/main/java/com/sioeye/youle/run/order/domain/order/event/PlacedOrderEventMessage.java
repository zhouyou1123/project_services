package com.sioeye.youle.run.order.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class PlacedOrderEventMessage {
    private String orderId;
    private String userId;
    private String parkId;
    private String combineSearchId;
    private Integer deviceType;
    private List<Goods> goodsList;
    private Date placeOrderDate;
    private Date paymentDate;

    @Getter
    @AllArgsConstructor
    public static class Goods{
        private String goodsId;
        private Integer goodsType;
        private String resourceId;
        private Integer resourceType;
        private Integer resourceCategory;
    }
}
