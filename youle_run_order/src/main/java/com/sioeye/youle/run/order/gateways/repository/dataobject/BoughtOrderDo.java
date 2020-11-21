package com.sioeye.youle.run.order.gateways.repository.dataobject;

import java.util.Date;

/**
 * 小程序个人中心--已购列表
 * 
 * @Author GuoGongwei
 * @Time: 2019年9月16日
 * @Description:
 */
public class BoughtOrderDo {

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
	// 类型
	private Integer goodsType;
	// 资源信息
	private Integer resourceCategory;
	private Integer resourceType;
	// 项目名称
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmusementparkId() {
		return amusementparkId;
	}

	public void setAmusementparkId(String amusementparkId) {
		this.amusementparkId = amusementparkId;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getResourceCategory() {
		return resourceCategory;
	}

	public void setResourceCategory(Integer resourceCategory) {
		this.resourceCategory = resourceCategory;
	}

	public Integer getResourceType() {
		return resourceType;
	}

	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Date getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getThumnailUrl() {
		return thumnailUrl;
	}

	public void setThumnailUrl(String thumnailUrl) {
		this.thumnailUrl = thumnailUrl;
	}

	public Integer getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(Integer itemStatus) {
		this.itemStatus = itemStatus;
	}

}
