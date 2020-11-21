package com.sioeye.youle.run.payment.config;

/**
 * author zhouyou ckt email:jinx.zhou@ck-telecom.com 2017年6月5日
 * EnumOrdersStatus.java description 订单状态
 */
public enum EnumOrdersStatus {

    UN_PAID(0, "unpaid"), PAY_SUCCESS(1, "pay success");

    private Integer code;
    private String message;

    EnumOrdersStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
