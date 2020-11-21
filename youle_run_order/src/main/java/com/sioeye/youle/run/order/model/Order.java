package com.sioeye.youle.run.order.model;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author zhouyou
 *
 * @ckt email:jinx.zhou@ck-telecom.com
 *
 * @date 2018年5月29日
 *
 * @fileName Order.java
 *
 * @todo 订单
 */
public class Order {

	private String objectId = UUID.randomUUID().toString().replace("-", "").toUpperCase();// 唯一编号;
	private String paymentId;// 支付id
	private String usersId;// 用户id
	private String amusementParkId;// 游乐园id
	private String parkName;// 游乐园名称
	private String activityId;// 活动id
	private String gameId;// 项目id
	private String gameName;// 项目名称
	private Integer count;// 视频数量
	private Double originalAmount;// 原价
	private Double promotionAmount;// 促销金额
	private String promotionId;// 优惠劵id
	private Double actualAmount;// 实际支付金额
	private Integer status;// 订单状态：0：未支付，1：已支付
	private Date orderTime;// 订单时间
	private Date updateTime;// 修改时间
	private Integer type;// 类型0：视频片段1：视频
	private Integer orderType;// 订单类型0：正常订单1：优惠订单2：免费订单
	private Boolean weishiShare;// 是否分享到微视
	private String weishiShareUrl;// 分享到微信url
	private String shareId;// 分享活动id
	private String shareName;// 分享活动名称
	private Integer shareUploadFlag;// 分享活动的视频是否可以上传
	private String parkShareId;// 游乐园分享活动id

	public Order() {

	}
	public Order(com.sioeye.youle.run.order.domain.order.Order order){
		this.objectId=order.id();
		this.paymentId=order.getPaymentId().id();
		this.usersId=order.getBuyer().id();
		this.status=order.getOrderStatus().getCode();
		this.orderTime=order.getPlaceOrderDate();
		this.updateTime=order.getPaymentDate();
		this.activityId=order.getActivity();
		this.amusementParkId=order.getPark().id();
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getUsersId() {
		return usersId;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getAmusementParkId() {
		return amusementParkId;
	}

	public void setAmusementParkId(String amusementParkId) {
		this.amusementParkId = amusementParkId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(Double originalAmount) {
		this.originalAmount = originalAmount;
	}

	public Double getPromotionAmount() {
		return promotionAmount;
	}

	public void setPromotionAmount(Double promotionAmount) {
		this.promotionAmount = promotionAmount;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public Double getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(Double actualAmount) {
		this.actualAmount = actualAmount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Boolean getWeishiShare() {
		return weishiShare;
	}

	public void setWeishiShare(Boolean weishiShare) {
		this.weishiShare = weishiShare;
	}

	public String getWeishiShareUrl() {
		return weishiShareUrl;
	}

	public void setWeishiShareUrl(String weishiShareUrl) {
		this.weishiShareUrl = weishiShareUrl;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getShareName() {
		return shareName;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}

	public Integer getShareUploadFlag() {
		return shareUploadFlag;
	}

	public void setShareUploadFlag(Integer shareUploadFlag) {
		this.shareUploadFlag = shareUploadFlag;
	}

	public String getParkShareId() {
		return parkShareId;
	}

	public void setParkShareId(String parkShareId) {
		this.parkShareId = parkShareId;
	}

}