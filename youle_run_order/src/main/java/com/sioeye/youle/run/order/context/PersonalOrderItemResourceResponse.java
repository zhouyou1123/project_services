package com.sioeye.youle.run.order.context;

import com.alibaba.fastjson.annotation.JSONField;
import com.sioeye.youle.run.order.context.codec.GoodsTypeEnumCodec;
import com.sioeye.youle.run.order.domain.goods.GoodsTypeEnum;
import com.sioeye.youle.run.order.domain.order.FilingStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PersonalOrderItemResourceResponse {
    private String orderItemId;
    private Date createDate;
    /**
     * 原始单价
     */
    private BigDecimal price;
    private BigDecimal actualAmount;
    private Integer count;
    private String previewUrl;
    private String downloadUrl;
    private String thumbnailUrl;
    @JSONField(serialize = false)
    private FilingStatusEnum filingStatus;
    private String goodsId;
    private Integer type;
    private String parkId;
    private String parkName;
    private String gameId;
    private String gameName;
}
