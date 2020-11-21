package com.sioeye.youle.run.order.gateways.repository.dataobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * orderitem
 * 
 * @author
 */
public class OrderItemDo implements Serializable {
	private String objectid;

	private String orderid;

	/**
	 * 用户id
	 */
	private String userid;

	private String username;

	private Integer count;

	/**
	 * 币种
	 */
	private String currency;

	private BigDecimal originalamount;

	private BigDecimal promotionamount;

	private BigDecimal actualamount;

	/**
	 * 优惠类型：0:原价，1：普通优惠，2：套餐优惠，3：分享活动优惠（具体分享活动优惠见sharename）
	 */
	private Integer promotiontype;

	/**
	 * 0:视频片段1:视频2:集锦3:照片,4:套票
	 */
	private Integer goodstype;

	private String goodsid;

	private String goodsname;

	private BigDecimal goodsprice;

	private String activityid;

	private String amusementparkid;

	private String parkname;

	private String gameid;

	private String gamename;

	private String seatid;

	private String seatsequenceno;

	private String seatmark;

	/**
	 * 0:未支付 1:已支付，未迁移 2:已支付，已迁移
	 */
	private Integer status;

	/**
	 * 预览地址
	 */
	private String previewurl;

	private String thumbnailurl;

	private Date createtime;

	private Date updatetime;

	private String shareid;

	private String sharename;

	/**
	 * 0：无须上传 1：上传
	 */
	private Boolean shareuploadflag;

	private String parkshareid;

	private String shareurl;

	private String sharecheckurl;

	/**
	 * 下载地址
	 */
	private String downloadurl;

	/**
	 * 资源信息
	 */
	private Integer resourcetype;
	private String resourcename;
	private Integer resourcecategory;

	private static final long serialVersionUID = 1L;

	public String getObjectid() {
		return objectid;
	}

	public void setObjectid(String objectid) {
		this.objectid = objectid;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public BigDecimal getActualamount() {
		return actualamount;
	}

	public void setActualamount(BigDecimal actualamount) {
		this.actualamount = actualamount;
	}

	public Integer getPromotiontype() {
		return promotiontype;
	}

	public void setPromotiontype(Integer promotiontype) {
		this.promotiontype = promotiontype;
	}

	public Integer getGoodstype() {
		return goodstype;
	}

	public void setGoodstype(Integer goodstype) {
		this.goodstype = goodstype;
	}

	public String getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public BigDecimal getGoodsprice() {
		return goodsprice;
	}

	public void setGoodsprice(BigDecimal goodsprice) {
		this.goodsprice = goodsprice;
	}

	public String getActivityid() {
		return activityid;
	}

	public void setActivityid(String activityid) {
		this.activityid = activityid;
	}

	public String getAmusementparkid() {
		return amusementparkid;
	}

	public void setAmusementparkid(String amusementparkid) {
		this.amusementparkid = amusementparkid;
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

	public String getSeatid() {
		return seatid;
	}

	public void setSeatid(String seatid) {
		this.seatid = seatid;
	}

	public String getSeatsequenceno() {
		return seatsequenceno;
	}

	public void setSeatsequenceno(String seatsequenceno) {
		this.seatsequenceno = seatsequenceno;
	}

	public String getSeatmark() {
		return seatmark;
	}

	public void setSeatmark(String seatmark) {
		this.seatmark = seatmark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPreviewurl() {
		return previewurl;
	}

	public void setPreviewurl(String previewurl) {
		this.previewurl = previewurl;
	}

	public String getThumbnailurl() {
		return thumbnailurl;
	}

	public void setThumbnailurl(String thumbnailurl) {
		this.thumbnailurl = thumbnailurl;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
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

	public Boolean getShareuploadflag() {
		return shareuploadflag;
	}

	public void setShareuploadflag(Boolean shareuploadflag) {
		this.shareuploadflag = shareuploadflag;
	}

	public String getParkshareid() {
		return parkshareid;
	}

	public void setParkshareid(String parkshareid) {
		this.parkshareid = parkshareid;
	}

	public String getShareurl() {
		return shareurl;
	}

	public void setShareurl(String shareurl) {
		this.shareurl = shareurl;
	}

	public String getSharecheckurl() {
		return sharecheckurl;
	}

	public void setSharecheckurl(String sharecheckurl) {
		this.sharecheckurl = sharecheckurl;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public Integer getResourcetype() {
		return resourcetype;
	}

	public void setResourcetype(Integer resourcetype) {
		this.resourcetype = resourcetype;
	}

	public String getResourcename() {
		return resourcename;
	}

	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}

	public Integer getResourceCategory() {
		return resourcecategory;
	}

	public void setResourceCategory(Integer resourcecategory) {
		this.resourcecategory = resourcecategory;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		OrderItemDo other = (OrderItemDo) that;
		return (this.getObjectid() == null ? other.getObjectid() == null
				: this.getObjectid().equals(other.getObjectid()))
				&& (this.getOrderid() == null ? other.getOrderid() == null
						: this.getOrderid().equals(other.getOrderid()))
				&& (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
				&& (this.getUsername() == null ? other.getUsername() == null
						: this.getUsername().equals(other.getUsername()))
				&& (this.getCount() == null ? other.getCount() == null : this.getCount().equals(other.getCount()))
				&& (this.getCurrency() == null ? other.getCurrency() == null
						: this.getCurrency().equals(other.getCurrency()))
				&& (this.getOriginalamount() == null ? other.getOriginalamount() == null
						: this.getOriginalamount().equals(other.getOriginalamount()))
				&& (this.getPromotionamount() == null ? other.getPromotionamount() == null
						: this.getPromotionamount().equals(other.getPromotionamount()))
				&& (this.getActualamount() == null ? other.getActualamount() == null
						: this.getActualamount().equals(other.getActualamount()))
				&& (this.getPromotiontype() == null ? other.getPromotiontype() == null
						: this.getPromotiontype().equals(other.getPromotiontype()))
				&& (this.getGoodstype() == null ? other.getGoodstype() == null
						: this.getGoodstype().equals(other.getGoodstype()))
				&& (this.getGoodsid() == null ? other.getGoodsid() == null
						: this.getGoodsid().equals(other.getGoodsid()))
				&& (this.getGoodsname() == null ? other.getGoodsname() == null
						: this.getGoodsname().equals(other.getGoodsname()))
				&& (this.getGoodsprice() == null ? other.getGoodsprice() == null
						: this.getGoodsprice().equals(other.getGoodsprice()))
				&& (this.getActivityid() == null ? other.getActivityid() == null
						: this.getActivityid().equals(other.getActivityid()))
				&& (this.getAmusementparkid() == null ? other.getAmusementparkid() == null
						: this.getAmusementparkid().equals(other.getAmusementparkid()))
				&& (this.getParkname() == null ? other.getParkname() == null
						: this.getParkname().equals(other.getParkname()))
				&& (this.getGameid() == null ? other.getGameid() == null : this.getGameid().equals(other.getGameid()))
				&& (this.getGamename() == null ? other.getGamename() == null
						: this.getGamename().equals(other.getGamename()))
				&& (this.getSeatid() == null ? other.getSeatid() == null : this.getSeatid().equals(other.getSeatid()))
				&& (this.getSeatsequenceno() == null ? other.getSeatsequenceno() == null
						: this.getSeatsequenceno().equals(other.getSeatsequenceno()))
				&& (this.getSeatmark() == null ? other.getSeatmark() == null
						: this.getSeatmark().equals(other.getSeatmark()))
				&& (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
				&& (this.getPreviewurl() == null ? other.getPreviewurl() == null
						: this.getPreviewurl().equals(other.getPreviewurl()))
				&& (this.getThumbnailurl() == null ? other.getThumbnailurl() == null
						: this.getThumbnailurl().equals(other.getThumbnailurl()))
				&& (this.getCreatetime() == null ? other.getCreatetime() == null
						: this.getCreatetime().equals(other.getCreatetime()))
				&& (this.getUpdatetime() == null ? other.getUpdatetime() == null
						: this.getUpdatetime().equals(other.getUpdatetime()))
				&& (this.getShareid() == null ? other.getShareid() == null
						: this.getShareid().equals(other.getShareid()))
				&& (this.getSharename() == null ? other.getSharename() == null
						: this.getSharename().equals(other.getSharename()))
				&& (this.getShareuploadflag() == null ? other.getShareuploadflag() == null
						: this.getShareuploadflag().equals(other.getShareuploadflag()))
				&& (this.getParkshareid() == null ? other.getParkshareid() == null
						: this.getParkshareid().equals(other.getParkshareid()))
				&& (this.getShareurl() == null ? other.getShareurl() == null
						: this.getShareurl().equals(other.getShareurl()))
				&& (this.getSharecheckurl() == null ? other.getSharecheckurl() == null
						: this.getSharecheckurl().equals(other.getSharecheckurl()))
				&& (this.getDownloadurl() == null ? other.getDownloadurl() == null
						: this.getDownloadurl().equals(other.getDownloadurl()))
				&& (this.getResourcetype() == null ? other.getResourcetype() == null
						: this.getResourcetype().equals(other.getResourcetype()))
				&& (this.getResourcename() == null ? other.getResourcename() == null
						: this.getResourcename().equals(other.getResourcename()))
				&& (this.getResourceCategory() == null ? other.getResourceCategory() == null
						: this.getResourceCategory().equals(other.getResourceCategory()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getObjectid() == null) ? 0 : getObjectid().hashCode());
		result = prime * result + ((getOrderid() == null) ? 0 : getOrderid().hashCode());
		result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
		result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
		result = prime * result + ((getCount() == null) ? 0 : getCount().hashCode());
		result = prime * result + ((getCurrency() == null) ? 0 : getCurrency().hashCode());
		result = prime * result + ((getOriginalamount() == null) ? 0 : getOriginalamount().hashCode());
		result = prime * result + ((getPromotionamount() == null) ? 0 : getPromotionamount().hashCode());
		result = prime * result + ((getActualamount() == null) ? 0 : getActualamount().hashCode());
		result = prime * result + ((getPromotiontype() == null) ? 0 : getPromotiontype().hashCode());
		result = prime * result + ((getGoodstype() == null) ? 0 : getGoodstype().hashCode());
		result = prime * result + ((getGoodsid() == null) ? 0 : getGoodsid().hashCode());
		result = prime * result + ((getGoodsname() == null) ? 0 : getGoodsname().hashCode());
		result = prime * result + ((getGoodsprice() == null) ? 0 : getGoodsprice().hashCode());
		result = prime * result + ((getActivityid() == null) ? 0 : getActivityid().hashCode());
		result = prime * result + ((getAmusementparkid() == null) ? 0 : getAmusementparkid().hashCode());
		result = prime * result + ((getParkname() == null) ? 0 : getParkname().hashCode());
		result = prime * result + ((getGameid() == null) ? 0 : getGameid().hashCode());
		result = prime * result + ((getGamename() == null) ? 0 : getGamename().hashCode());
		result = prime * result + ((getSeatid() == null) ? 0 : getSeatid().hashCode());
		result = prime * result + ((getSeatsequenceno() == null) ? 0 : getSeatsequenceno().hashCode());
		result = prime * result + ((getSeatmark() == null) ? 0 : getSeatmark().hashCode());
		result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
		result = prime * result + ((getPreviewurl() == null) ? 0 : getPreviewurl().hashCode());
		result = prime * result + ((getThumbnailurl() == null) ? 0 : getThumbnailurl().hashCode());
		result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
		result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
		result = prime * result + ((getShareid() == null) ? 0 : getShareid().hashCode());
		result = prime * result + ((getSharename() == null) ? 0 : getSharename().hashCode());
		result = prime * result + ((getShareuploadflag() == null) ? 0 : getShareuploadflag().hashCode());
		result = prime * result + ((getParkshareid() == null) ? 0 : getParkshareid().hashCode());
		result = prime * result + ((getShareurl() == null) ? 0 : getShareurl().hashCode());
		result = prime * result + ((getSharecheckurl() == null) ? 0 : getSharecheckurl().hashCode());
		result = prime * result + ((getDownloadurl() == null) ? 0 : getDownloadurl().hashCode());
		result = prime * result + ((getResourcetype() == null) ? 0 : getResourcetype().hashCode());
		result = prime * result + ((getResourcename() == null) ? 0 : getResourcename().hashCode());
		result = prime * result + ((getResourceCategory() == null) ? 0 : getResourceCategory().hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", objectid=").append(objectid);
		sb.append(", orderid=").append(orderid);
		sb.append(", userid=").append(userid);
		sb.append(", username=").append(username);
		sb.append(", count=").append(count);
		sb.append(", currency=").append(currency);
		sb.append(", originalamount=").append(originalamount);
		sb.append(", promotionamount=").append(promotionamount);
		sb.append(", actualamount=").append(actualamount);
		sb.append(", promotiontype=").append(promotiontype);
		sb.append(", goodstype=").append(goodstype);
		sb.append(", goodsid=").append(goodsid);
		sb.append(", goodsname=").append(goodsname);
		sb.append(", goodsprice=").append(goodsprice);
		sb.append(", activityid=").append(activityid);
		sb.append(", amusementparkid=").append(amusementparkid);
		sb.append(", parkname=").append(parkname);
		sb.append(", gameid=").append(gameid);
		sb.append(", gamename=").append(gamename);
		sb.append(", seatid=").append(seatid);
		sb.append(", seatsequenceno=").append(seatsequenceno);
		sb.append(", seatmark=").append(seatmark);
		sb.append(", status=").append(status);
		sb.append(", previewurl=").append(previewurl);
		sb.append(", thumbnailurl=").append(thumbnailurl);
		sb.append(", createtime=").append(createtime);
		sb.append(", updatetime=").append(updatetime);
		sb.append(", shareid=").append(shareid);
		sb.append(", sharename=").append(sharename);
		sb.append(", shareuploadflag=").append(shareuploadflag);
		sb.append(", parkshareid=").append(parkshareid);
		sb.append(", shareurl=").append(shareurl);
		sb.append(", sharecheckurl=").append(sharecheckurl);
		sb.append(", downloadurl=").append(downloadurl);
		sb.append(", resourcetype=").append(resourcetype);
		sb.append(", resourcename=").append(resourcename);
		sb.append(", resourcecategory=").append(resourcecategory);
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append("]");
		return sb.toString();
	}
}