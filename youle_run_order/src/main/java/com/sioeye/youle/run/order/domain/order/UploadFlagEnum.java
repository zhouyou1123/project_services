package com.sioeye.youle.run.order.domain.order;

public enum UploadFlagEnum {

    /**
     * 不上传
     */
    NO(0),
    /**
     * 上传
     */
    YES(1);

    private int code;

    public int getCode(){
        return code;
    }
    UploadFlagEnum(int code){
        this.code=code;
    }
}
