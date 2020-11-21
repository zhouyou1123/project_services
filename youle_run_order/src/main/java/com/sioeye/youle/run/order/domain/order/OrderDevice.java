package com.sioeye.youle.run.order.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderDevice {
    private String deviceRecordId;
    private DeviceTypeEnum deviceType;

    public static OrderDevice build(String deviceRecordId){
        return new OrderDevice(deviceRecordId,DeviceTypeEnum.MINIPROGRAM);
    }
}
