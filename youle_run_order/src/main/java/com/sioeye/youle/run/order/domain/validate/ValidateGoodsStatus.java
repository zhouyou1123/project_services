package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ValidateGoodsStatus {
    default void validate(ValidateGoodsStatusContext context, Function<Integer, GoodsPrice> onShelf, Consumer<Goods> addGoods){
        GoodsPrice apply = onShelf.apply(context.goodsType());
        if (apply!=null){
            //商品在售，才能查询详细信息
            Goods goods = getGoods(context);
            addGoods.accept(goods);
        }

    }
//    Goods getGoods(String goodsId);

    Goods getGoods(ValidateGoodsStatusContext context);
}
