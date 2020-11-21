package com.sioeye.youle.run.order.config;

import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class GoodsTypeEnumConverter implements Converter<String, GoodsTypeEnum> {
    @Override
    public GoodsTypeEnum convert(String source) {
        try {
            return GoodsTypeEnum.valueOf(Integer.parseInt(source));
        }catch (Exception ex){
            throw new RuntimeException("GoodsTypeEnum converter is failure.");
        }
    }
}
