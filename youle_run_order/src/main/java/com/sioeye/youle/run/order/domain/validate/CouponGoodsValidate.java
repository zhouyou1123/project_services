package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import org.springframework.stereotype.Component;

@Component
public class CouponGoodsValidate implements ValidateDuplicateBuy,ValidateGoodsStatus {

    protected BoughtService buyerCouponService;
    protected AdminParkService parkService;

    public CouponGoodsValidate(BoughtService buyerCouponService, AdminParkService parkService){
        this.buyerCouponService = buyerCouponService;
        this.parkService = parkService;
    }

    @Override
    public void validate(ValidateDuplicateBuyContext context) {
        if (context.needValidate()){
            validateCouponGoodsDuplicateBuy(context.userId(),context.parkId(),context.goodsId());
        }
    }

    private void validateCouponGoodsDuplicateBuy(String buyerId,String parkId,String goodsId){
        //对于套票，首先验证BuyerCoupon ，就是判断用户购买的套票有效期；其次判断已支付订单的套票的有效期、再次判断第三方支付成功的有效期
        if(buyerCouponService.getBuyerCoupon(buyerId,parkId) != null){
            throw new CustomException(DomainErrorCodeEnum.DUPLICATE_BUY.getCode(),String.format(DomainErrorCodeEnum.DUPLICATE_BUY.getMessage(),goodsId));
        }
    }

    @Override
    public Goods getGoods(ValidateGoodsStatusContext context) {
        CouponGoods coupon = parkService.getCoupon(context.goodsId(),context.name());
        coupon.validateCouponOverdue();
        return coupon;
    }
}
