package com.sioeye.youle.run.order.context.codec;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;

import java.io.IOException;
import java.lang.reflect.Type;

public class GoodsTypeEnumCodec implements ObjectSerializer, ObjectDeserializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        GoodsTypeEnum goodsType=(GoodsTypeEnum)o;
        jsonSerializer.write(goodsType.getCode());
    }

    @Override
    public GoodsTypeEnum deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
        int code = defaultJSONParser.getLexer().intValue();
        return GoodsTypeEnum.valueOf(code);
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
