package com.sioeye.youle.run.order.config;

/**
 * 
 * @author zhouyou 
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2019年1月10日
 *
 * @fileName EnumHighlightStatus.java 
 *
 * @todo 合成视频集锦状态
 */
public enum EnumHighlightStatus {

	MERGING(0, "merging"), MERGE_SUCCESS(1, "merge success"),MERGE_PAY_SUCCESS(2,"");

    private Integer code;
    private String message;

    EnumHighlightStatus(Integer code, String message) {
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
