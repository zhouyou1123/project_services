package com.sioeye.youle.run.order.interfaces;

import com.sioeye.youle.run.order.domain.goods.CouponGoods;
import com.sioeye.youle.run.order.domain.goods.Print1Add1Goods;
import com.sioeye.youle.run.order.domain.park.ParkShareActivity;
import com.sioeye.youle.run.order.domain.price.ParkGoodsPrice;
import com.sioeye.youle.run.order.domain.price.ParkPrice;

public interface AdminParkService {


    @Deprecated
    public CouponGoods getCoupon(String couponId,String couponName);

    @Deprecated
    public ParkPrice getPrice(String parkId, String gameId);

    public ParkGoodsPrice getParkGoodsPrice(String parkId,String gameId);
}
