package com.sioeye.youle.run.order.context;

import com.sioeye.youle.run.order.config.EnumTradeType;
import com.sioeye.youle.run.order.domain.order.DeviceTypeEnum;
import com.sioeye.youle.run.order.domain.order.DiscountTypeEnum;
import com.sioeye.youle.run.order.domain.order.PromotionTypeEnum;
import com.sioeye.youle.run.order.domain.payment.PayWayEnum;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
@Data
public class CreateOrderRequest {


    private String amusementParkId;
    @Deprecated
    private String parkName;
    @Deprecated
    private String activityId;
    private BigDecimal originalAmount = BigDecimal.ZERO;
    private BigDecimal actualAmount;
    private String openId;
    @Deprecated
    private EnumTradeType tradeType;
    @Deprecated
    private List<String> goodsIds;
    @Deprecated
    private String paymentDescription;
    private String spbillCreateIp = "127.0.0.1";
    @Deprecated
    private String gameId;
    @Deprecated
    private String gameName;
    @Deprecated
    private String goodsName;
    @Deprecated
    private String goodsDesc;
    private String userId;
    private PayWayEnum payWay = PayWayEnum.YEEPAY;
    private DiscountTypeEnum discountType = DiscountTypeEnum.FULL;
    private PromotionTypeEnum promotionType = PromotionTypeEnum.FULL;
    private String parkShareActivityId;
    private List<OrderGoods> goodsList;
    //设备的记录id
    private String deviceRecordId;
    //打印（场景）id
    private String sceneId;
    //设备类型
    private DeviceTypeEnum deviceType = DeviceTypeEnum.MINIPROGRAM;

    /**
     * 小程序表单id
     */
    private String formId;
    /**
     * 搜索id
     */
    private String searchId;

}
