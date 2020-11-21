package com.sioeye.youle.run.order.domain.price;

import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
@Getter
@AllArgsConstructor
public class ParkPrice extends ValueObject{
    @JSONField(name = "objectId")
    private String parkId;
    private boolean stopped;
    private boolean enabled;
    private BigDecimal clipPrice;
    private BigDecimal highlightPrice;
    private BigDecimal photoPrice;
    private BigDecimal photoPrintPrice;
    private String currency;
    private String timeZone;
    private PromotionPrice promotionPrice;
    private CouponPrice couponPrice;
    private List<ShareActivityPrice> shareActivityPriceList;
    public ParkPrice(){}
}
