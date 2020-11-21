package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import org.springframework.stereotype.Component;

@Component
public class GameCouponGoodsValidate extends CouponGoodsValidate {
    public GameCouponGoodsValidate(BoughtService buyerCouponService, AdminParkService parkService){
       super(buyerCouponService,parkService);
    }

    @Override
    public void validate(ValidateDuplicateBuyContext context) {
        if (context.needValidate()){
            validateCouponGoodsDuplicateBuy(context.userId(),context.parkId(),context.goodsId());
        }
    }

    private void validateCouponGoodsDuplicateBuy(String buyerId,String parkId,String goodsId){
        //购买项目套餐时，
        // 1、先验证全园套餐是否购买和未过期；
        // 2、在验证项目套餐本身是否购买和未过期；
        if(buyerCouponService.getBuyerCoupon(buyerId,parkId) != null){
            throw new CustomException(DomainErrorCodeEnum.DUPLICATE_COUPON_ERROR.getCode(),String.format(DomainErrorCodeEnum.DUPLICATE_COUPON_ERROR.getMessage(),goodsId));
        }
        if(buyerCouponService.getBuyerGameCoupon(buyerId,goodsId) != null){
            throw new CustomException(DomainErrorCodeEnum.DUPLICATE_BUY.getCode(),String.format(DomainErrorCodeEnum.DUPLICATE_BUY.getMessage(),goodsId));
        }
    }
}
