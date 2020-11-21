package com.sioeye.youle.run.order.config;


import com.sioeye.youle.run.order.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {


    /**
     * 全局异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public String GlobalExceptionHandler(Exception e) {

        ExceptionUtil.log(log::error,e);
        return "{\"success\":false,\"code\":\"110500\",\"message\":\"".concat(e.getMessage()).concat("\"}");
    }


}
