package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.config.EnumHandle;
import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.OrderRepository;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.OrderContext;
import com.sioeye.youle.run.order.domain.price.GoodsPriceConfiguration;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.PresentPrice;
import com.sioeye.youle.run.order.domain.price.PromotionContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Component
public class PresentPromotionValidateGoods implements ValidateGoodsPromotion {

    private OrderRepository orderRepository;
    public PresentPromotionValidateGoods(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Override
    public BigDecimal doCalculatePrice(GoodsContext goodsContext, ParkGoodsPrice parkGoodsPrice, OrderContext orderContext, PromotionContext promotionContext) {


        Optional<GoodsPriceConfiguration> goodsPriceConfig = parkGoodsPrice
                .getGoodsPriceConfig(goodsContext.goodsType());
        if (!goodsPriceConfig.isPresent()) {
            throw new CustomException(DomainErrorCodeEnum.GOODS_SOLD_OUT.getCode(),
                    String.format(DomainErrorCodeEnum.GOODS_SOLD_OUT.getMessage(),
                            "goodsType:" + goodsContext.goodsType().toString()));
        }
        if (goodsPriceConfig.get().getPresentPrice() == null) {
            throw new RuntimeException("present price is null.");
        }
        PresentPrice presentPrice = goodsPriceConfig.get().getPresentPrice();
        String timeZone = parkGoodsPrice.getTimeZone();
        String parkId = parkGoodsPrice.getParkId();
        // 优惠活动过期，直接返回原始价格
        if (!presentPrice.validateInPromotionPeriod(timeZone)) {
            throw new RuntimeException("present is overdue.");
        }
        // 1.获取赠送活动的本地开始时间和结束时间
        Date localStartDate = presentPrice.calcLocalPeriodStartDate(timeZone);
        Date localEndDate = presentPrice.calcLocalPeriodEndDate(timeZone);

        int orderCountByPresent = orderRepository.getOrderCountByPresent(orderContext.userId(), parkId, goodsContext.goodsType(),
                localStartDate, localEndDate);

        if (!presentPrice.validateUsePromotion(orderCountByPresent, timeZone)) {
            throw new CustomException(EnumHandle.NOT_SUPPORT_PARKDISCOUNT.getCode(),String.format(EnumHandle.NOT_SUPPORT_PARKDISCOUNT.getMessage(),"Present:4"));
        }
        return BigDecimal.ZERO;
    }
}
