package com.sioeye.youle.run.order.domain.buyer;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.common.ValueObject;
import com.sioeye.youle.run.order.domain.goods.Park;
import lombok.Getter;

import javax.validation.constraints.Max;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Getter
public class BuyerCoupon extends ValueObject {
    private Buyer buyer;
    private String timeZone;
    private Date couponStartDate;
    private Date couponEndDate;
    private String orderId;
    private Park park;
    private Collection<Integer> canBuyGoodsTypes;
    private Integer level = 0;
    private String gameId;
    private String couponId;

    public BuyerCoupon(Buyer buyer,Date couponStartDate,Date couponEndDate,String timeZone,String orderId,Park park,
                       Collection<Integer> canBuyGoodsTypes,Integer level,String gameId,String couponId){
        this.buyer = buyer;
        this.couponStartDate = couponStartDate;
        this.couponEndDate = couponEndDate;
        this.timeZone = timeZone;
        this.orderId = orderId;
        this.park = park;
        this.canBuyGoodsTypes = canBuyGoodsTypes;
        this.level = level;
        this.gameId = gameId;
        this.couponId = couponId;
    }

    /**
     * 验证用户购买的套餐是否过期
     * @param placeOrderDate
     * @return true 过期，false 未过期
     */
    public boolean validatePeriodOverdue(Date placeOrderDate){

        ZoneId parkTimeZone = ZoneId.of(timeZone);

        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(couponStartDate.toInstant(), parkTimeZone);
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(couponEndDate.toInstant(), parkTimeZone);
        LocalDateTime placeOrderLocalDateTime = LocalDateTime.ofInstant(placeOrderDate.toInstant(), parkTimeZone);
        if (startLocalDateTime.isAfter(placeOrderLocalDateTime) || endLocalDateTime.isBefore(placeOrderLocalDateTime)){
            return true;
        }
        return false;
    }
    public boolean validateCanBuyGoodsType(Integer goodsType){
        if (canBuyGoodsTypes==null || canBuyGoodsTypes.size()<1 || goodsType==null){
            //套票购买商品范围为空，就不能购买；
            return false;
        }
        return canBuyGoodsTypes.contains(goodsType);
    }
}
