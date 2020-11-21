package com.sioeye.youle.run.order.util;


import com.alibaba.fastjson.JSONObject;

import java.util.Optional;
import java.util.function.Consumer;

public class ExceptionUtil {
    public static String log(Consumer<String> consumer,Exception exception){
        return log(consumer,exception,Optional.empty());
    }
    public static  <T> String log(Consumer<String> consumer, Exception exception, Optional<T> param){
        if (exception == null){
            return null;
        }
        JSONObject jsonObject = new JSONObject(5);
        param.ifPresent(t->jsonObject.put("param",t));
        jsonObject.put("errorMessage",exception.getMessage());
        jsonObject.put("errorStack",exception.getStackTrace().length>0?exception.getStackTrace()[0].toString():"null");
        String message = jsonObject.toJSONString();
        consumer.accept(message);
        return message;
    }

}
