package com.sioeye.youle.run.order.domain.order;

public enum OrderStatusEnum {
    /**
     * 未支付
     */
    NOT_PAY(0),
    /**
     * 以支付
     */
    PAID(1);

    private int code;

    public int getCode(){
        return code;
    }
    OrderStatusEnum(int code){
        this.code=code;
    }

    public static OrderStatusEnum valueOf(int code){
        for (OrderStatusEnum orderStatus : values()){
            if (orderStatus.getCode() == code){
                return orderStatus;
            }
        }
        throw new RuntimeException(String.format("orderStatus not support code:%s",code));
    }
}
