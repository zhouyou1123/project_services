package com.sioeye.youle.run.order.domain.order;

import java.util.UUID;

public class OrderIdUtils {

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    public static String generatorOrderItemId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    public static String combineSearchId(){
        return uuid();
    }

}
