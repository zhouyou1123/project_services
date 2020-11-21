package com.sioeye.youle.run.order.domain.goods;

import lombok.Getter;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
public class GameCouponGoods extends CouponGoods{

    public GameCouponGoods(String id, String goodsName, Park park, int days, String timeZone, Date createDate,
                       Collection<Integer> canBuyGoodsTypes, Boolean isInPublishPeriod, Boolean enabled,Game game) {
        super(id,GoodsTypeEnum.GAMECOUPON,goodsName,park,days,timeZone,createDate,canBuyGoodsTypes,isInPublishPeriod,enabled);
        level =1;
        this.game = game;
    }

    public GameCouponGoods(String id, String goodsName, Park park, int days, String timeZone, Date createDate,
                       Date startDate, Date endDate, List<Integer> canBuyGoodsTypes,Game game) {
        super(id,GoodsTypeEnum.GAMECOUPON,goodsName,park,days,timeZone,createDate,startDate,endDate,canBuyGoodsTypes);
        level =1;
        this.game = game;

    }

    @Override
    public String toExtends() {
        return "{" + "\"timeZone\":\"" + timeZone + "\"," + "\"startDate\":" + startDate.getTime() + ","
                + "\"endDate\":" + endDate.getTime() + "," + "\"days\":" + days + "," + "\"level\":" + level + ","
                + "\"gameId\":\"" + game.id() + "\"," + "\"gameName\":\"" + game.getGameName() + "\"," + "\"canBuyGoodsTypes\":[" + canBuyGoodsTypesToString() + "]" + "}";
    }


}
