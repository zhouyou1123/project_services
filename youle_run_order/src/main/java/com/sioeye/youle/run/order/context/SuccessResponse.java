package com.sioeye.youle.run.order.context;

import lombok.Data;

@Data
public class SuccessResponse<T> extends BaseResponse  {
    private T value;

    public static <T> SuccessResponse<T> build(T t){
        SuccessResponse<T> response = new SuccessResponse<>();
        response.setValue(t);
        response.setSuccess(true);
        return response;
    }
}
