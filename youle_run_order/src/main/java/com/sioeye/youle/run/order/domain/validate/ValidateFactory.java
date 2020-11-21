package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.PromotionTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ValidateFactory implements BeanFactoryAware {

    private Integer defaultGoodsType = 0;

    private Map<Integer, Object> validateMapList = new HashMap<>(2);
    private Map<Integer,Object> validateAmountMapList = new HashMap<>(1);
    private Map<Integer,Object> validatePromotionMapList = new HashMap<>(4);
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        //商品
//        validateMapList.put(GoodsTypeEnum.CLIP.getCode(),beanFactory.getBean(ClipGoodsValidate.class));
//        validateMapList.put(GoodsTypeEnum.PHOTO.getCode(),beanFactory.getBean(PhotoGoodsValidate.class));
        validateMapList.put(defaultGoodsType,beanFactory.getBean(ResourceGoodsValidate.class));
        validateMapList.put(GoodsTypeEnum.PRINT.getCode(),beanFactory.getBean(PrintGoodsValidate.class));
        validateMapList.put(GoodsTypeEnum.COUPON.getCode(),beanFactory.getBean("couponGoodsValidate"));
        validateMapList.put(GoodsTypeEnum.PRINT1ADD1.getCode(),beanFactory.getBean(Print1Add1GoodsValidate.class));
        validateMapList.put(GoodsTypeEnum.GAMECOUPON.getCode(),beanFactory.getBean("gameCouponGoodsValidate"));
        //金额
        validateAmountMapList.put(PromotionTypeEnum.FULL.getCode(),beanFactory.getBean(FullAmountValidate.class));
        //商品优惠
        validatePromotionMapList.put(PromotionTypeEnum.FULL.getCode(),beanFactory.getBean(FullPromotionValidateGoods.class));
        validatePromotionMapList.put(PromotionTypeEnum.PARKDISCOUNT.getCode(),beanFactory.getBean(GeneralPromotionValidateGoods.class));
        validatePromotionMapList.put(PromotionTypeEnum.COUPON.getCode(),beanFactory.getBean(CouponPromotionValidateGoods.class));
        validatePromotionMapList.put(PromotionTypeEnum.SHARE.getCode(),beanFactory.getBean(ShareActivityPromotionValidateGoods.class));
        validatePromotionMapList.put(PromotionTypeEnum.PRESENT.getCode(),beanFactory.getBean(PresentPromotionValidateGoods.class));


    }
    public ValidateDuplicateBuy getValidateDuplicateBuy(Integer goodsType){
        Object validate = validateMapList.get(goodsType);
        if (validate == null){
            validate = validateMapList.get(defaultGoodsType);
        }
        return (ValidateDuplicateBuy)validate;
    }
    public ValidateGoodsStatus getValidateGoodsStatus(Integer goodsType){
        Object validate = validateMapList.get(goodsType);
        if (validate == null){
            validate = validateMapList.get(defaultGoodsType);
        }
        return (ValidateGoodsStatus)validate;
    }
    public ValidateAmount getValidateAmount(PromotionTypeEnum promotionTypeEnum){
        //TODO 验证订单金额现阶段只支持原价，后续可以考虑支持优惠劵和满减
        return (ValidateAmount)validateAmountMapList.computeIfAbsent(PromotionTypeEnum.FULL.getCode(),(key)->{
            throw new RuntimeException(String.format("promotionType(discountType:%s) not support.",key));
        });
    }
    public ValidateGoodsPromotion getValidateGoodsPromotion(PromotionTypeEnum promotionTypeEnum){
        return (ValidateGoodsPromotion) validatePromotionMapList.get(promotionTypeEnum.getCode());
    }

}
