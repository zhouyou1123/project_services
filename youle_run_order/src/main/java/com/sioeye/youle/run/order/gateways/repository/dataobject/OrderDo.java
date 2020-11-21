package com.sioeye.youle.run.order.gateways.repository.dataobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * order
 * 
 * @author
 */
public class OrderDo implements Serializable {
	private String objectid;

	private String paymentid;

	private String usersid;

	private String amusementparkid;

	private String activityid;

	private Short count;

	private BigDecimal originalamount;

	private BigDecimal promotionamount;

	private String promotionid;

	private BigDecimal actualamount;

	/**
	 * 0：未支付，1：已支付
	 */
	private Short status;

	/**
	 * 订单创建时间（下单时间）
	 */
	private Date ordertime;

	/**
	 * 订单支付时间
	 */
	private Date updatetime;

	/**
	 * 0:视频片段 1:视频 2:集锦 3:照片 4:套餐 5:照片打印 6:打印1+1
	 */
	private Short type;

	/**
	 * 优惠类型：0:原价，1：普通优惠，2：套餐优惠，3：分享活动优惠（具体分享活动优惠见sharename）
	 */
	private Short ordertype;

	/**
	 * 是否是分享到微视
	 */
	private Boolean weishishare;

	/**
	 * 微视观看地址
	 */
	private String weishishareurl;

	private String parkname;

	private String gameid;

	private String gamename;

	/**
	 * 分享活动id
	 */
	private String shareid;

	/**
	 * 分享活动名称
	 */
	private String sharename;

	/**
	 * 是否上传（0不上传，1上传）
	 */
	private Short shareuploadflag;

	/**
	 * 游乐园分享活动ID
	 */
	private String parkshareid;

	/**
	 * 打印设备记录id
	 */
	private String devicerecordid;

	/**
	 * 设备类型
	 */
	private Integer devicetype;
	/**
	 * 搜索id
	 */
	private String searchid;

	private static final long serialVersionUID = 1L;

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	public String getPaymentid() {
		return paymentid;
	}

	public void setPaymentid(String paymentid) {
		this.paymentid = paymentid;
	}

	public String getUsersid() {
		return usersid;
	}

	public void setUsersid(String usersid) {
		this.usersid = usersid;
	}

	public String getAmusementparkid() {
		return amusementparkid;
	}

	public void setAmusementparkid(String amusementparkid) {
		this.amusementparkid = amusementparkid;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}

	public Short getCount() {
		return count;
	}

	public void setCount(Short count) {
		this.count = count;
	}

	public BigDecimal getOriginalamount() {
		return originalamount;
	}

	public void setOriginalamount(BigDecimal originalamount) {
		this.originalamount = originalamount;
	}

	public BigDecimal getPromotionamount() {
		return promotionamount;
	}

	public void setPromotionamount(BigDecimal promotionamount) {
		this.promotionamount = promotionamount;
	}

	public String getPromotionid() {
		return promotionid;
	}

	public void setPromotionid(String promotionid) {
		this.promotionid = promotionid;
	}

	public BigDecimal getActualamount() {
		return actualamount;
	}

	public void setActualamount(BigDecimal actualamount) {
		this.actualamount = actualamount;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Date getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Short getOrdertype() {
		return ordertype;
	}

	public void setOrdertype(Short ordertype) {
		this.ordertype = ordertype;
	}

	public Boolean getWeishishare() {
		return weishishare;
	}

	public void setWeishishare(Boolean weishishare) {
		this.weishishare = weishishare;
	}

	public String getWeishishareurl() {
		return weishishareurl;
	}

	public void setWeishishareurl(String weishishareurl) {
		this.weishishareurl = weishishareurl;
	}

	public String getParkname() {
		return parkname;
	}

	public void setParkname(String parkname) {
		this.parkname = parkname;
	}

	public String getGameid() {
		return gameid;
	}

	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}

	public String getShareid() {
		return shareid;
	}

	public void setShareid(String shareid) {
		this.shareid = shareid;
	}

	public String getSharename() {
		return sharename;
	}

	public void setSharename(String sharename) {
		this.sharename = sharename;
	}

	public Short getShareuploadflag() {
		return shareuploadflag;
	}

	public void setShareuploadflag(Short shareuploadflag) {
		this.shareuploadflag = shareuploadflag;
	}

	public String getParkshareid() {
		return parkshareid;
	}

	public void setParkshareid(String parkshareid) {
		this.parkshareid = parkshareid;
	}

	public String getDevicerecordid() {
		return devicerecordid;
	}

	public void setDevicerecordid(String devicerecordid) {
		this.devicerecordid = devicerecordid;
	}

	public Integer getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(Integer devicetype) {
		this.devicetype = devicetype;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		OrderDo orderDo = (OrderDo) o;

		if (objectid != null ? !objectid.equals(orderDo.objectid) : orderDo.objectid != null)
			return false;
		if (paymentid != null ? !paymentid.equals(orderDo.paymentid) : orderDo.paymentid != null)
			return false;
		if (usersid != null ? !usersid.equals(orderDo.usersid) : orderDo.usersid != null)
			return false;
		if (amusementparkid != null ? !amusementparkid.equals(orderDo.amusementparkid)
				: orderDo.amusementparkid != null)
			return false;
		if (activityid != null ? !activityid.equals(orderDo.activityid) : orderDo.activityid != null)
			return false;
		if (count != null ? !count.equals(orderDo.count) : orderDo.count != null)
			return false;
		if (originalamount != null ? !originalamount.equals(orderDo.originalamount) : orderDo.originalamount != null)
			return false;
		if (promotionamount != null ? !promotionamount.equals(orderDo.promotionamount)
				: orderDo.promotionamount != null)
			return false;
		if (promotionid != null ? !promotionid.equals(orderDo.promotionid) : orderDo.promotionid != null)
			return false;
		if (actualamount != null ? !actualamount.equals(orderDo.actualamount) : orderDo.actualamount != null)
			return false;
		if (status != null ? !status.equals(orderDo.status) : orderDo.status != null)
			return false;
		if (ordertime != null ? !ordertime.equals(orderDo.ordertime) : orderDo.ordertime != null)
			return false;
		if (updatetime != null ? !updatetime.equals(orderDo.updatetime) : orderDo.updatetime != null)
			return false;
		if (type != null ? !type.equals(orderDo.type) : orderDo.type != null)
			return false;
		if (ordertype != null ? !ordertype.equals(orderDo.ordertype) : orderDo.ordertype != null)
			return false;
		if (weishishare != null ? !weishishare.equals(orderDo.weishishare) : orderDo.weishishare != null)
			return false;
		if (weishishareurl != null ? !weishishareurl.equals(orderDo.weishishareurl) : orderDo.weishishareurl != null)
			return false;
		if (parkname != null ? !parkname.equals(orderDo.parkname) : orderDo.parkname != null)
			return false;
		if (gameid != null ? !gameid.equals(orderDo.gameid) : orderDo.gameid != null)
			return false;
		if (gamename != null ? !gamename.equals(orderDo.gamename) : orderDo.gamename != null)
			return false;
		if (shareid != null ? !shareid.equals(orderDo.shareid) : orderDo.shareid != null)
			return false;
		if (sharename != null ? !sharename.equals(orderDo.sharename) : orderDo.sharename != null)
			return false;
		if (shareuploadflag != null ? !shareuploadflag.equals(orderDo.shareuploadflag)
				: orderDo.shareuploadflag != null)
			return false;
		if (parkshareid != null ? !parkshareid.equals(orderDo.parkshareid) : orderDo.parkshareid != null)
			return false;
		if (devicerecordid != null ? !devicerecordid.equals(orderDo.devicerecordid) : orderDo.devicerecordid != null)
			return false;
		return devicetype != null ? devicetype.equals(orderDo.devicetype) : orderDo.devicetype == null;

	}

	@Override
	public int hashCode() {
		int result = objectid != null ? objectid.hashCode() : 0;
		result = 31 * result + (paymentid != null ? paymentid.hashCode() : 0);
		result = 31 * result + (usersid != null ? usersid.hashCode() : 0);
		result = 31 * result + (amusementparkid != null ? amusementparkid.hashCode() : 0);
		result = 31 * result + (activityid != null ? activityid.hashCode() : 0);
		result = 31 * result + (count != null ? count.hashCode() : 0);
		result = 31 * result + (originalamount != null ? originalamount.hashCode() : 0);
		result = 31 * result + (promotionamount != null ? promotionamount.hashCode() : 0);
		result = 31 * result + (promotionid != null ? promotionid.hashCode() : 0);
		result = 31 * result + (actualamount != null ? actualamount.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (ordertime != null ? ordertime.hashCode() : 0);
		result = 31 * result + (updatetime != null ? updatetime.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (ordertype != null ? ordertype.hashCode() : 0);
		result = 31 * result + (weishishare != null ? weishishare.hashCode() : 0);
		result = 31 * result + (weishishareurl != null ? weishishareurl.hashCode() : 0);
		result = 31 * result + (parkname != null ? parkname.hashCode() : 0);
		result = 31 * result + (gameid != null ? gameid.hashCode() : 0);
		result = 31 * result + (gamename != null ? gamename.hashCode() : 0);
		result = 31 * result + (shareid != null ? shareid.hashCode() : 0);
		result = 31 * result + (sharename != null ? sharename.hashCode() : 0);
		result = 31 * result + (shareuploadflag != null ? shareuploadflag.hashCode() : 0);
		result = 31 * result + (parkshareid != null ? parkshareid.hashCode() : 0);
		result = 31 * result + (devicerecordid != null ? devicerecordid.hashCode() : 0);
		result = 31 * result + (devicetype != null ? devicetype.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "OrderDo{" + "objectid='" + objectid + '\'' + ", paymentid='" + paymentid + '\'' + ", usersid='"
				+ usersid + '\'' + ", amusementparkid='" + amusementparkid + '\'' + ", activityid='" + activityid + '\''
				+ ", count=" + count + ", originalamount=" + originalamount + ", promotionamount=" + promotionamount
				+ ", promotionid='" + promotionid + '\'' + ", actualamount=" + actualamount + ", status=" + status
				+ ", ordertime=" + ordertime + ", updatetime=" + updatetime + ", type=" + type + ", ordertype="
				+ ordertype + ", weishishare=" + weishishare + ", weishishareurl='" + weishishareurl + '\''
				+ ", parkname='" + parkname + '\'' + ", gameid='" + gameid + '\'' + ", gamename='" + gamename + '\''
				+ ", shareid='" + shareid + '\'' + ", sharename='" + sharename + '\'' + ", shareuploadflag="
				+ shareuploadflag + ", parkshareid='" + parkshareid + '\'' + ", devicerecordid='" + devicerecordid
				+ '\'' + ", devicetype=" + devicetype + '}';
	}
}