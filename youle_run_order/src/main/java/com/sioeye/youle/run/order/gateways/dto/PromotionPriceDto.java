package com.sioeye.youle.run.order.gateways.dto;

import com.sioeye.youle.run.order.domain.price.PromotionPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PromotionPriceDto {
    private Integer fullPriceOrderCount;
    private Integer promotionOrderLimit;
    private BigDecimal promotionPrice;
    private Date startTime;
    private Date endTime;
    private Boolean enabled;
    private Boolean isInPeriod;
    private String summary;
    private String description;

    public PromotionPrice toPromotionPrice(BigDecimal originalPrice){
        return  new PromotionPrice(fullPriceOrderCount,promotionOrderLimit,this.promotionPrice,originalPrice,startTime,endTime,enabled,summary,description);
    }
}
