package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
@Data
public class OrderTradeType {
    private EnumTradeType tradeType;
    private String paymentDescription;
    private Integer orderType;
    private Boolean needValidateDuplicate;
    private List<TradeGoodsType> goodsList;
    private List<TradeGoodsType> extendGoodsList;

    public Stream<TradeGoodsType> goodsListStream(){
        if (goodsList == null){
            return Stream.empty();
        }
        return goodsList.stream();
    }
    public Stream<TradeGoodsType> extendGoodsListStream(){
        if (extendGoodsList == null){
            return Stream.empty();
        }
        return extendGoodsList.stream();
    }

    public boolean checkNeedCalcAmount(GoodsTypeEnum goodsTypeEnum){
        Optional<TradeGoodsType> tradeGoodsType = goodsListStream().filter(goods -> GoodsTypeEnum.equals(goods.getGoodsType(), goodsTypeEnum)).findAny();
        if (tradeGoodsType.isPresent()){
            return tradeGoodsType.get().getNeedCalcAmount();
        }
        tradeGoodsType = extendGoodsListStream().filter(goods -> GoodsTypeEnum.equals(goods.getGoodsType(), goodsTypeEnum)).findAny();
        if (tradeGoodsType.isPresent()){
            return tradeGoodsType.get().getNeedCalcAmount();
        }
        throw new RuntimeException(String.format("%s not contains (%s)",this.tradeType,goodsTypeEnum));
    }

    public void checkLackGoodsType(List<GoodsTypeEnum> goodsTypeList){
        goodsListStream().forEach((tradeGoods)->{
            goodsTypeList.stream().filter(type-> GoodsTypeEnum.equals(tradeGoods.getGoodsType(),type)).findAny()
                    .orElseThrow(()-> new RuntimeException(String.format("%s miss goodsType(%s)",this.tradeType,tradeGoods.getGoodsType())));
        });
    }
    public void checkInvalidGoodsType(List<GoodsTypeEnum> goodsTypeList){
        goodsTypeList.stream().forEach(goodsType->{
            goodsListStream().filter(tradeGoods->GoodsTypeEnum.equals(tradeGoods.getGoodsType(), goodsType)).findAny()
                    .orElseThrow(()-> new RuntimeException(String.format("%s contain invalid goodsType(%s)",this.tradeType,goodsType)));
        });
    }

}
