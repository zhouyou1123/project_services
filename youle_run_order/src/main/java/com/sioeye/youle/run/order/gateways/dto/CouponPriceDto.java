package com.sioeye.youle.run.order.gateways.dto;

import com.sioeye.youle.run.order.domain.price.CouponPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@Data
public class CouponPriceDto {
    private String couponId;
    private BigDecimal couponPrice;
    private Collection<Integer> goodsTypes;
    private Date publishStart;
    private Date publishEnd;
    private Integer useDays;
    private BigDecimal maxOffers;
    private Integer validGamesCount;
    private Boolean enabled;
    private Boolean isInPublishPeriod;
    private String summary;
    private String description;

    public CouponPrice toCouponPrice(){
        return new CouponPrice(couponId,couponPrice,maxOffers,validGamesCount,goodsTypes,publishStart,publishEnd,useDays,enabled,summary,description);
    }
}
