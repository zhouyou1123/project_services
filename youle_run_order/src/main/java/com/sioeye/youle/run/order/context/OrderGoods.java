package com.sioeye.youle.run.order.context;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.goods.GoodsContext;
import com.sioeye.youle.run.order.domain.resource.ResourceCategory;
import com.sioeye.youle.run.order.domain.resource.ResourceContext;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

@Data
public class OrderGoods implements GoodsContext, ResourceContext {
    private String goodsId;
    private Integer goodsType;
    private Integer resourceType;
    private ResourceCategory resourceCategory;
    private Boolean needValidateOnShelf = true;
    private Integer goodsCount = 1;
    private CompletableFuture<String> goodsIdCompletableFuture;

    public OrderGoods(String goodsId,Integer goodsType){
        this(goodsId,goodsType,1);
    }
    public OrderGoods(String goodsId,Integer goodsType,Integer goodsCount){
        this(goodsId,goodsType,null,null,goodsCount,null);
    }
    public OrderGoods(String goodsId,Integer resourceType,ResourceCategory resourceCategory){
        this(goodsId,null,resourceType,resourceCategory,1,null);
    }
    public OrderGoods(Integer resourceType,ResourceCategory resourceCategory,CompletableFuture<String> goodsIdCompletableFuture){
        this(null,null,resourceType,resourceCategory,1,goodsIdCompletableFuture);
    }
    public OrderGoods(String goodsId,Integer goodsType,Integer resourceType,ResourceCategory resourceCategory,Integer goodsCount,CompletableFuture<String> goodsIdCompletableFuture){
        this.goodsId = goodsId;
        this.goodsType = goodsType;
        this.resourceType = resourceType;
        this.resourceCategory = resourceCategory;
        this.goodsCount =goodsCount;
        this.goodsIdCompletableFuture = goodsIdCompletableFuture;
    }
    public String getGoodsId(){
        if (goodsId == null){
            goodsId = goodsIdCompletableFuture.join();
        }
        return goodsId;
    }
    @Override
    public String goodsId() {
        return getGoodsId();
    }

    @Override
    public Integer goodsType() {
        return goodsType;
    }

    @Override
    public String resourceId() {
        return getGoodsId();
    }

    @Override
    public ResourceCategory resourceCategory() {
        return resourceCategory;
    }

    @Override
    public Integer resourceType() {
        return resourceType;
    }
}
