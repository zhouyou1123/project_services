package com.sioeye.youle.run.order.domain.order;

public enum FilingStatusEnum {

    /**
     * 未归档
     */
    NOTFILING(0),
    /**
     * 归档中
     */
    FILING(1),
    /**
     * 已归档
     */
    AREADYFILING(2);

    private int code;

    public int getCode(){
        return code;
    }
    FilingStatusEnum(int code){
        this.code=code;
    }
    public static FilingStatusEnum valueOf(Integer code){
        for (FilingStatusEnum filingStatus : values()){
            if (filingStatus.getCode()==code){
                return filingStatus;
            }
        }
        throw new RuntimeException(String.format("filingStatus not support code:%s",code));
    }

}
