package com.sioeye.youle.run.order.domain.validate;

import com.sioeye.youle.run.order.config.CustomException;
import com.sioeye.youle.run.order.domain.DomainErrorCodeEnum;
import com.sioeye.youle.run.order.domain.goods.Goods;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.goods.Park;
import com.sioeye.youle.run.order.domain.goods.Print1Add1Goods;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.interfaces.AdminParkService;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class Print1Add1GoodsValidate implements ValidateDuplicateBuy,ValidateGoodsStatus {
    private AdminParkService parkService;

    public Print1Add1GoodsValidate(AdminParkService parkService){
        this.parkService = parkService;
    }


    @Override
    public void validate(ValidateGoodsStatusContext context, Function<Integer, GoodsPrice> onShelf, Consumer<Goods> addGoods) {
        GoodsPrice goodsPrice = onShelf.apply(context.goodsType());
        if (goodsPrice==null){
            throw new CustomException(DomainErrorCodeEnum.GOODS_SOLD_OUT.getCode(),String.format(DomainErrorCodeEnum.GOODS_SOLD_OUT.getMessage(),"goodsType:"+context.goodsType().toString()));
        }
        Park park = new Park(goodsPrice.getParkId(),goodsPrice.getParkName());
        addGoods.accept(new Print1Add1Goods(goodsPrice.getPriceId(),goodsPrice.getName(),park));
    }

    @Override
    public Goods getGoods(ValidateGoodsStatusContext context) {
        return null;
    }

    /**
     * 验证重复购买
     * @param context
     */
    @Override
    public void validate(ValidateDuplicateBuyContext context) {
        //  打印1+1 不需要验证重复购买
    }
}
