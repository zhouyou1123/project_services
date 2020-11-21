package com.sioeye.youle.run.order.domain.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.sioeye.youle.run.order.context.OrderGoods;
import com.sioeye.youle.run.order.domain.price.GoodsPrice;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;

import lombok.Getter;

@Getter
public class OrderGoodsMappingContext {
    private List<GoodsMappingResource> goodsMappingResourceList;

    private boolean needValidateDuplicate;
    private String paymentDescription;

    private OrderGoodsMappingContext(List<GoodsMappingResource> goodsMappingResourceList,boolean needValidateDuplicate,String paymentDescription){
        this.goodsMappingResourceList = goodsMappingResourceList;
        this.needValidateDuplicate = needValidateDuplicate;
        this.paymentDescription = paymentDescription;
    }

    public boolean checkNeedCalcAmount(Integer goodsType){
        return goodsMappingResourceList.stream().filter(goods->goods.goodsType().equals(goodsType)).map(GoodsMappingResource::getNeedCalcAmount).findAny().orElse(false);
    }
    public Integer getOrderType(){
        return goodsMappingResourceList.get(0).goodsType();
    }

    public static OrderGoodsMappingContext build(ParkGoodsPrice parkGoodsPrice, List<OrderGoods> goodsList){
        if (parkGoodsPrice == null || goodsList == null || goodsList.size()<1){
            throw new RuntimeException("parkGoodsPrice is null or goodsList is null.");
        }
        //1、取出第一个商品为主商品 和 主商品的价格配置
        GoodsPrice mainGoodsPrice =getMainGoodsPrice(parkGoodsPrice,goodsList);
        //2、验证前端输入的资源，在价格配置中是否存在，不存在抛出异常
        checkGoodsParamsExist(mainGoodsPrice,goodsList);

        //3 构建 商品资源映射列表
        List<GoodsMappingResource> goodsMappingResourceList = mappingGoodsResources(mainGoodsPrice,parkGoodsPrice,goodsList);
        //4、验证少传的商品
        checkMissGoodsParam(mainGoodsPrice,goodsMappingResourceList);
        return new OrderGoodsMappingContext(goodsMappingResourceList,mainGoodsPrice.getSaleConfig().getNeedValidateDuplicate(),mainGoodsPrice.getSaleConfig().getPaymentDescription());
    }
    private static GoodsPrice getMainGoodsPrice(ParkGoodsPrice parkGoodsPrice, List<OrderGoods> goodsList){
        OrderGoods mainGoods = goodsList.get(0);
        Optional<GoodsPrice> mainGoodsPriceOptional = null;
        if (mainGoods.getGoodsType()!=null){
            //前端传了商品类型
            mainGoodsPriceOptional = parkGoodsPrice.getGoodsPrice(mainGoods.goodsType());
        }else{
            //前端传的是资源类型
            mainGoodsPriceOptional = parkGoodsPrice.getGoodsPriceByResourceType(mainGoods.getResourceType());
        }
        return mainGoodsPriceOptional.orElseThrow(()->new RuntimeException(String.format("goodsId(%s) be not find goods price.",mainGoods.getGoodsId())));
    }
    private static void checkGoodsParamsExist(GoodsPrice mainGoodsPrice, List<OrderGoods> goodsList){
        goodsList.stream().skip(1).forEach(goods->{
            if (goods.getGoodsType()!=null){
                //根据商品类型进行查找
                if(!mainGoodsPrice.checkGoodsExist(goods.getGoodsType())){
                    throw new RuntimeException(String.format("invalid goodsType(%s)",goods.getGoodsType()));
                }
            }else{
                //根据资源类型进行查找
                if (!mainGoodsPrice.checkResourceExist(goods.getResourceType())){
                    throw new RuntimeException(String.format("invalid resourceType(%s)",goods.getResourceType()));
                }
            }
        });
    }
    private static List<GoodsMappingResource> mappingGoodsResources(GoodsPrice mainGoodsPrice,ParkGoodsPrice parkGoodsPrice,List<OrderGoods> goodsList){
        OrderGoods mainGoods = goodsList.get(0);
        List<GoodsMappingResource> goodsMappingResourceList = new ArrayList<>();
        //主商品
        GoodsMappingResource goodsMappingResource = new GoodsMappingResource(mainGoods.getGoodsId(),mainGoodsPrice.getType(),mainGoodsPrice.getName(),mainGoods.getResourceType(),mainGoods.getResourceCategory(),true);
        goodsMappingResourceList.add(goodsMappingResource);
        //其他商品,其他商品默认不计算价格
        goodsList.stream().skip(1).forEach(goods->{
            GoodsMappingResource goodsResource = null;
            GoodsPrice goodsPrice = null;
            if (goods.getGoodsType()!=null){
                goodsPrice = parkGoodsPrice.getGoodsPrice(goods.getGoodsType()).orElseThrow(() -> new RuntimeException(String.format("get goodsPrice is failture,invalid goodsType(%s)",goods.getGoodsType())));
                goodsResource = new GoodsMappingResource(goods.getGoodsId(),goods.getGoodsType(),goodsPrice.getName(),goods.getResourceType(),goods.getResourceCategory(),false);
                goodsMappingResourceList.add(goodsResource);
            }else{
                goodsPrice = parkGoodsPrice.getGoodsPriceByResourceType(goods.getResourceType()).orElseThrow(() -> new RuntimeException(String.format("get goodsPrice is failture,invalid resourceType(%s)",goods.getResourceType())));
                goodsResource = new GoodsMappingResource(goods.getGoodsId(),goodsPrice.getType(),goodsPrice.getName(),goods.getResourceType(),goods.getResourceCategory(),false);
                goodsMappingResourceList.add(goodsResource);
            }
        });
        return goodsMappingResourceList;
    }
    private static void checkMissGoodsParam(GoodsPrice mainGoodsPrice,List<GoodsMappingResource> goodsMappingResourceList){
        //验证是否缺少主商品
        goodsMappingResourceList.stream().filter(g->g.goodsType().equals(mainGoodsPrice.getType())).findAny().orElseThrow(()->new RuntimeException(String.format("miss goodsType(%s)",mainGoodsPrice.getType())));
        if (mainGoodsPrice.getSaleConfig().getValidGoods()!=null){

            Arrays.stream(mainGoodsPrice.getSaleConfig().getValidGoods()).filter(gv-> goodsMappingResourceList.stream().filter(g-> g.goodsType().equals(gv)).findAny().isPresent()).findAny().orElseThrow(()->new RuntimeException(String.format("miss goodsType(%s)",mainGoodsPrice.getSaleConfig().getValidGoods())));
        }
        if (mainGoodsPrice.getSaleConfig().getValidResource()!=null){
            Arrays.stream(mainGoodsPrice.getSaleConfig().getValidResource()).flatMap(vg-> Arrays.stream(vg)).filter(gv->goodsMappingResourceList.stream().filter(g->g.resourceType()!=null).filter(g-> g.resourceType().equals(gv)).findAny().isPresent()).findAny().orElseThrow(()->new RuntimeException(String.format("miss resourceType(%s)",mainGoodsPrice.getSaleConfig().getValidResource())));
        }
    }
    public String getGoodsPricePaymentDescription(String parkName, String gameName) {
		StringBuffer result = new StringBuffer(paymentDescription);
		Optional.ofNullable(parkName).ifPresent(name -> {
			result.append("-");
			result.append(name);
		});
		Optional.ofNullable(gameName).ifPresent(name -> {
			result.append("-");
			result.append(name);
		});
		return result.toString();
	}
}
