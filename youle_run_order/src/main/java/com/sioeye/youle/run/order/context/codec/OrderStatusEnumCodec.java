package com.sioeye.youle.run.order.context.codec;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.sioeye.youle.run.order.domain.order.OrderStatusEnum;

import java.io.IOException;
import java.lang.reflect.Type;

public class OrderStatusEnumCodec implements ObjectSerializer, ObjectDeserializer {

    @Override
    public OrderStatusEnum deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        int code = parser.getLexer().intValue();
        return OrderStatusEnum.valueOf(code);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        OrderStatusEnum statusEnum=(OrderStatusEnum)object;
        serializer.write(statusEnum.getCode());
    }
}
