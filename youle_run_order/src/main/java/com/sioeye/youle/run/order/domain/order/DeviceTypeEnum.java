package com.sioeye.youle.run.order.domain.order;

public enum DeviceTypeEnum {
    MINIPROGRAM(0),H5PRINT(1),TV(2),ADDEVICE(3),TRAFFICPERMIT(4) ;

    private Integer code;

    DeviceTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    public  static DeviceTypeEnum valueOf(Integer code){
        for (DeviceTypeEnum type: values()) {
            if (type.getCode() == code){
                return type;
            }
        }
        throw new RuntimeException(String.format("code(%s) can not Change DeviceTypeEnum.",code));
    }


}
