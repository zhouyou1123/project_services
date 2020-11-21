package com.sioeye.youle.run.order.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorResponse extends BaseResponse{
    private String code;
    private String message;

    public static  ErrorResponse build(String code,String message){
        ErrorResponse response = new ErrorResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }
}
