package com.sioeye.youle.run.order.context.codec;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;

import java.io.IOException;
import java.lang.reflect.Type;

public class PayWayEnumCodec implements ObjectSerializer, ObjectDeserializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        PayWayEnum payWay=(PayWayEnum)o;
        jsonSerializer.write(payWay.getCode());
    }

    @Override
    public PayWayEnum deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
        int code = defaultJSONParser.getLexer().intValue();
        return PayWayEnum.valueOf(code);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
