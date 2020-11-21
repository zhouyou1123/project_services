package com.sioeye.youle.run.order.model;

import java.io.Serializable;
import java.util.Date;

/**
 * orderitem
 * @author 
 */
public class OrderItem implements Serializable {
    private String objectid;

    private String orderid;

    /**
     * 0:视频片段1:视频2:集锦3:照片,4:套票
     */
    private Integer goodstype;

    private String goodsid;

    private String goodsname;

    private String amusementparkid;

    private String parkname;

    private String gameid;

    private String gamename;

    private String seatid;

    private String seatsequenceno;

    private String seatmark;

    /**
     * 0未支付1：已支付
     */
    private Integer status;

    private String url;

    private String thumbnailurl;

    private Date createtime;

    private Date updatetime;

    /**
     * 0: 未归档
1: 已归档
     */
    private Integer filingstatus;

    /**
     * 0:不上传
1:上传
     */
    private Integer uploadflag;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Integer getFilingstatus() {
        return filingstatus;
    }

    public void setFilingstatus(Integer filingstatus) {
        this.filingstatus = filingstatus;
    }

    public Integer getUploadflag() {
        return uploadflag;
    }

    public void setUploadflag(Integer uploadflag) {
        this.uploadflag = uploadflag;
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
        OrderItem other = (OrderItem) that;
        return (this.getObjectid() == null ? other.getObjectid() == null : this.getObjectid().equals(other.getObjectid()))
            && (this.getOrderid() == null ? other.getOrderid() == null : this.getOrderid().equals(other.getOrderid()))
            && (this.getGoodstype() == null ? other.getGoodstype() == null : this.getGoodstype().equals(other.getGoodstype()))
            && (this.getGoodsid() == null ? other.getGoodsid() == null : this.getGoodsid().equals(other.getGoodsid()))
            && (this.getGoodsname() == null ? other.getGoodsname() == null : this.getGoodsname().equals(other.getGoodsname()))
            && (this.getAmusementparkid() == null ? other.getAmusementparkid() == null : this.getAmusementparkid().equals(other.getAmusementparkid()))
            && (this.getParkname() == null ? other.getParkname() == null : this.getParkname().equals(other.getParkname()))
            && (this.getGameid() == null ? other.getGameid() == null : this.getGameid().equals(other.getGameid()))
            && (this.getGamename() == null ? other.getGamename() == null : this.getGamename().equals(other.getGamename()))
            && (this.getSeatid() == null ? other.getSeatid() == null : this.getSeatid().equals(other.getSeatid()))
            && (this.getSeatsequenceno() == null ? other.getSeatsequenceno() == null : this.getSeatsequenceno().equals(other.getSeatsequenceno()))
            && (this.getSeatmark() == null ? other.getSeatmark() == null : this.getSeatmark().equals(other.getSeatmark()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getThumbnailurl() == null ? other.getThumbnailurl() == null : this.getThumbnailurl().equals(other.getThumbnailurl()))
            && (this.getCreatetime() == null ? other.getCreatetime() == null : this.getCreatetime().equals(other.getCreatetime()))
            && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
            && (this.getFilingstatus() == null ? other.getFilingstatus() == null : this.getFilingstatus().equals(other.getFilingstatus()))
            && (this.getUploadflag() == null ? other.getUploadflag() == null : this.getUploadflag().equals(other.getUploadflag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getObjectid() == null) ? 0 : getObjectid().hashCode());
        result = prime * result + ((getOrderid() == null) ? 0 : getOrderid().hashCode());
        result = prime * result + ((getGoodstype() == null) ? 0 : getGoodstype().hashCode());
        result = prime * result + ((getGoodsid() == null) ? 0 : getGoodsid().hashCode());
        result = prime * result + ((getGoodsname() == null) ? 0 : getGoodsname().hashCode());
        result = prime * result + ((getAmusementparkid() == null) ? 0 : getAmusementparkid().hashCode());
        result = prime * result + ((getParkname() == null) ? 0 : getParkname().hashCode());
        result = prime * result + ((getGameid() == null) ? 0 : getGameid().hashCode());
        result = prime * result + ((getGamename() == null) ? 0 : getGamename().hashCode());
        result = prime * result + ((getSeatid() == null) ? 0 : getSeatid().hashCode());
        result = prime * result + ((getSeatsequenceno() == null) ? 0 : getSeatsequenceno().hashCode());
        result = prime * result + ((getSeatmark() == null) ? 0 : getSeatmark().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getThumbnailurl() == null) ? 0 : getThumbnailurl().hashCode());
        result = prime * result + ((getCreatetime() == null) ? 0 : getCreatetime().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getFilingstatus() == null) ? 0 : getFilingstatus().hashCode());
        result = prime * result + ((getUploadflag() == null) ? 0 : getUploadflag().hashCode());
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
        sb.append(", goodstype=").append(goodstype);
        sb.append(", goodsid=").append(goodsid);
        sb.append(", goodsname=").append(goodsname);
        sb.append(", amusementparkid=").append(amusementparkid);
        sb.append(", parkname=").append(parkname);
        sb.append(", gameid=").append(gameid);
        sb.append(", gamename=").append(gamename);
        sb.append(", seatid=").append(seatid);
        sb.append(", seatsequenceno=").append(seatsequenceno);
        sb.append(", seatmark=").append(seatmark);
        sb.append(", status=").append(status);
        sb.append(", url=").append(url);
        sb.append(", thumbnailurl=").append(thumbnailurl);
        sb.append(", createtime=").append(createtime);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", filingstatus=").append(filingstatus);
        sb.append(", uploadflag=").append(uploadflag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}