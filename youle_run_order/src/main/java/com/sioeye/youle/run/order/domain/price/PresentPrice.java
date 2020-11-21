package com.sioeye.youle.run.order.domain.price;

import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.Getter;

import java.util.Date;

@Getter
public class PresentPrice extends ValueObject {
     private String description;
     private Boolean enabled;
     private Date endTime;
     private Boolean isInPeriod;
     private Date startTime;
     private String summary;
    /**
     * 限制赠送的数量
     */
    private Integer limit = 1;

    public PresentPrice(){

    }

    public PresentPrice(String description,Boolean enabled,Date endTime,Boolean isInPeriod,Date startTime,String summary){
        this.description = description;
        this.enabled = enabled;
        this.endTime = endTime;
        this.isInPeriod = isInPeriod;
        this.startTime = startTime;
        this.summary = summary;
        limit = 1;
    }

    public Date calcLocalPeriodStartDate(String timeZone) {
        return startTime;
    }

    public Date calcLocalPeriodEndDate(String timeZone) {
        return endTime;
    }

    public Boolean validateInPromotionPeriod(String timeZone) {
        return enabled && isInPeriod;
    }

    /**
     * 验证用户是否享用赠送权益
     * @param presentCountPriceOrderCount
     * @param timeZone
     * @return
     */
    public Boolean validateUsePromotion(int presentCountPriceOrderCount, String timeZone) {
        if (this.enabled && this.validateInPromotionPeriod(timeZone) && presentCountPriceOrderCount<limit) {
            return true;
        }
        return false;
    }

}
