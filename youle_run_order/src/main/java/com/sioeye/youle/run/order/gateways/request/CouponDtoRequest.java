package com.sioeye.youle.run.order.gateways.request;

import com.alibaba.fastjson.annotation.JSONField;

public class CouponDtoRequest {
    private String couponId;

    @JSONField(name = "couponId")
    public String getCouponId(){
        return couponId;
    }
    public void setCouponId(String couponId){
        this.couponId=couponId;
    }

    public CouponDtoRequest(String couponId){
        this.couponId = couponId;
    }
}
