package com.sioeye.youle.run.order.domain.payment;

public enum PayWayEnum {
    WEIXIN(1), ALI(2), IOS(3), COUPON(4),LINE(5),PAYPAL(6),YEEPAY(7);

    private int code;

    PayWayEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static PayWayEnum valueOf(int code){
        for (PayWayEnum payWay : values()){
            if (payWay.getCode() == code){
                return payWay;
            }
        }
        throw new RuntimeException(String.format("payWay not support code:%s",code));
    }
}
