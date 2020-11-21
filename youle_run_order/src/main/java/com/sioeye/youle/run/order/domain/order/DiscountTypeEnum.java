package com.sioeye.youle.run.order.domain.order;

public enum DiscountTypeEnum {

    /**
     * 原价
     */
    FULL(0),
    /**
     * 游乐园折扣
     */
    PARKDISCOUNT(1),
    /**
     * 套票
     */
    COUPON(2),
    /**
     * 分享活动
     */
    SHARE(3),
    /**
     * 赠送
     */
    PRESENT(4);



    private int code;

    public int getCode(){
        return code;
    }
    DiscountTypeEnum(int code){
        this.code=code;
    }

    public static DiscountTypeEnum valueOf(int code){
        for (DiscountTypeEnum discountType : values()){
            if (discountType.getCode()==code){
                return discountType;
            }
        }
        throw new RuntimeException(String.format("discountType not support code:%s",code));
    }
}
