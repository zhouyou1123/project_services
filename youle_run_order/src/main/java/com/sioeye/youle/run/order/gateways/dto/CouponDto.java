package com.sioeye.youle.run.order.gateways.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
public class CouponDto {
    private String couponId;
    private String parkId;
    private String parkName;
    private String timezone;
    private String currency;
    private Set<Integer> goodsTypes;
    private Integer useDays;
    private BigDecimal price;
    private BigDecimal maxOffers;
    private Integer validGamesCount;
    private Boolean enabled;
    private Boolean isInPublishPeriod;
    private Date publishStart;
    private Date publishEnd;
    private Date createTime;
    private Date updateTime;
}
