package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.PhotoGoods;
import com.sioeye.youle.run.order.domain.goods.PrintGoods;
import com.sioeye.youle.run.order.domain.goods.ResourceGoods;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.resource.Resource;
import com.sioeye.youle.run.order.interfaces.RunCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class PrintGoodsValidate implements ValidateDuplicateBuy,ValidateGoodsStatus{
    RunCoreService runCoreService;
    public PrintGoodsValidate(RunCoreService runCoreService){
        this.runCoreService = runCoreService;
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
