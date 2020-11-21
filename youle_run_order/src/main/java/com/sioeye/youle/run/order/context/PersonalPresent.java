package com.sioeye.youle.run.order.context;

import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.context.codec.GoodsTypeEnumCodec;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import lombok.Data;

import java.util.Set;

@Data
public class PersonalPresent {
    private Integer goodsType;
    /**
     * 当前用户能否使用赠送权益
     */
    private Boolean usePresent;

    /**
     * 如果能使用赠送权益，需要返回当前用户已经购买的商品id列表
     */
    private Set<String> goodsIds;
}
