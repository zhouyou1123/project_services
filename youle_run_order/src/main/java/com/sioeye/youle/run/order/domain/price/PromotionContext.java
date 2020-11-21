package com.sioeye.youle.run.order.domain.price;

import com.sioeye.youle.run.order.domain.order.PromotionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PromotionContext {
    private PromotionTypeEnum promotionType;
    private String promotionId;
}
