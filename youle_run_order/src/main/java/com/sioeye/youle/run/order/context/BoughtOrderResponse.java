package com.sioeye.youle.run.order.context;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BoughtOrderResponse {

	// 订单id
	private String orderId;
	// 游乐园id
	private String amusementparkId;
	// 游乐园名称
	private String parkName;
	// 项目名称
	private String goodsName;
	// 商品id
	private String goodsId;
	// 商品类型
	private Integer goodsType;
	// 资源种类
	private Integer resourceCategory;
	// 资源类型
	private Integer resourceType;
	//项目名称
	private String gameName;
	// 订单优惠类型
	private Integer orderType;
	// 订单支付时间
	private Date paymentTime;
	// 订单支付状态
	private Integer status;
	// orderItem状态
	private Integer itemStatus;
	// 商品封面CDN预览地址
	private String thumnailUrl;
	//套餐到期时间
	private Date couponEndTime;
}
