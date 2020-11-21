package com.sioeye.youle.run.order.domain.price;

import lombok.Data;

import java.util.Collection;

@Data
public class GoodsPriceConfiguration {
    private GoodsPrice goodsPrice;
    private PromotionPrice promotionPrice;
    private PresentPrice presentPrice;
    private CouponPrice couponPrice;
    private Collection<ShareActivityPrice> sharePrice;
    
    public PromotionPrice getPromotionPrice() {
    	if(promotionPrice!=null && promotionPrice.getOriginalPrice() !=null) {
    		return promotionPrice;
    	}else {
    		if(goodsPrice==null || promotionPrice==null)return null;
    		promotionPrice = promotionPrice.build(goodsPrice.getPrice());
    		return promotionPrice;
    	}
    }
}
