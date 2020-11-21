package com.sioeye.youle.run.order.domain.price;

import com.sioeye.youle.run.order.domain.goods.GoodsCategory;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class GoodsPrice {
    private String priceId;
    private List<Integer> saleChannel;
    private BigDecimal price = BigDecimal.ZERO;
    private Boolean status = true;
    private Integer type;
    private String parkId;
    private String parkName;
    private GoodsSaleConfig saleConfig;
    private Integer goodsFlag=0;
    private String name;
    public Integer getGoodsType(){
        return type;
    }
    public GoodsCategory getGoodsCategory(){
        return GoodsCategory.valueOf(goodsFlag);
    }

    public boolean checkGoodsExist(Integer goodsType){
        if (saleConfig.getValidGoods()==null){
            return false;
        }
        return Arrays.stream(saleConfig.getValidGoods()).filter(goods->goods.equals(goodsType)).findAny().isPresent();
    }
    public boolean checkResourceExist(Integer resourceType){
        if (saleConfig.getValidResource()==null){
            return false;
        }
        return Arrays.stream(saleConfig.getValidResource()).flatMap(resource-> Arrays.stream(resource)).filter(resource->resource.equals(resourceType)).findAny().isPresent();
    }

}
