package com.sioeye.youle.run.order.domain.validate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.goods.ResourceGoods;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.domain.service.BoughtService;
import com.sioeye.youle.run.order.interfaces.RunCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class ResourceGoodsValidate implements ValidateDuplicateBuy, ValidateGoodsStatus  {

    private RunCoreService runCoreService;
    private BoughtService buyerCouponService;
    public ResourceGoodsValidate(BoughtService buyerCouponService, RunCoreService runCoreService) {
        this.runCoreService = runCoreService;
        this.buyerCouponService = buyerCouponService;
    }
    @Override
    public void validate(ValidateDuplicateBuyContext context) {
        // TODO 验证资源是否已经购买
        if (context.needValidate()) {
            validateClipGoodsDuplicateBuy(context.userId(), context.goodsId(),context.goodsType());
        }
    }
    private void validateClipGoodsDuplicateBuy(String userId, String goodsId, Integer goodsType) {
        if (buyerCouponService.checkIsBoughtGoods(userId, goodsId, goodsType).isPresent()) {
            throw new CustomException(DomainErrorCodeEnum.DUPLICATE_BUY.getCode(),
                    String.format(DomainErrorCodeEnum.DUPLICATE_BUY.getMessage(), goodsId));
        }
    }

    @Override
    public void validate(ValidateGoodsStatusContext context, Function<Integer, GoodsPrice> onShelf, Consumer<Goods> addGoods) {
        GoodsPrice apply = onShelf.apply(context.goodsType());
        if (apply!=null){
            //商品在售，才能查询详细信息
            Goods goods = getGoods(context);
            addGoods.accept(goods);
        }
    }
    @Override
    public Goods getGoods(ValidateGoodsStatusContext context){
//        log.info("{" +
//                "\"goodsId\":\"" + context.goodsId() +"\","+
//                "\"goodsType\":\"" + context.goodsType() +"\","+
//                "\"resourceId\":\"" + context.resourceId() +"\","+
//                "\"resourceType\":\"" + context.resourceType() +"\","+
//                "\"resourceCategory\":\"" + context.resourceCategory() +"\","+
//                "}");
        Resource resource =  runCoreService.getResource(context.goodsId(),context.resourceType(),context.resourceCategory());
        return new ResourceGoods(context.goodsType(),context.name(),resource);
    }
}
