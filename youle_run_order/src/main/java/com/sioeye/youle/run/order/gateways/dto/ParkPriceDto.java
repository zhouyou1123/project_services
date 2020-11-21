package com.sioeye.youle.run.order.gateways.dto;

import com.sioeye.youle.run.order.domain.price.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ParkPriceDto {
    private String objectId;
    private String parkName;
    private Boolean stopped;
    private Boolean enabled;
    private BigDecimal price;
    private BigDecimal highlightPrice;
    private BigDecimal photoPrice;
    private BigDecimal photoPrintPrice;
    private String currency;
    private String timezone;
    private PromotionPriceDto promotion;
    private CouponPriceDto coupon;
    private List<ShareActivityPriceDto> share;

    public ParkPrice toParkPrice(){

        PromotionPrice promotionPrice = promotion == null ? null: promotion.toPromotionPrice(price);
        CouponPrice couponPrice = coupon == null ? null: coupon.toCouponPrice();
        List<ShareActivityPrice> shareActivities = new ArrayList<>(share==null?0:share.size());
        if (share!=null){
            for (ShareActivityPriceDto shareActivityPriceDto : share){
                shareActivities.add(shareActivityPriceDto.toShareActivityPrice());
            }
        }
        Currency currency = new Currency(this.currency);

        return new ParkPrice(this.objectId,stopped,enabled,price,highlightPrice,photoPrice,photoPrintPrice,this.currency,timezone,promotionPrice,couponPrice,shareActivities);
    }
}
