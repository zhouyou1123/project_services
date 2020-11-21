package com.sioeye.youle.run.order.gateways.repository.dataobject;

import java.io.Serializable;
import java.util.Date;

/**
 * usercoupon
 * @author 
 */
public class UserCouponDo implements Serializable {
    private String objectid;

    private String userid;

    private String amusementparkid;

    private String parkname;

    private String openid;

    private String timezone;

    /**
     * 套票有效期开始日期
     */
    private Date startdate;

    /**
     * 套票有效期开始日期
     */
    private Date enddate;

    private Date updatetime;

    /**
     * 生成用户套票时的订单id
     */
    private String orderid;

    /**
     * 购买商品类型
     */
    private Integer[] canbuygoodstypes;

    /**
     * 套餐级别：0全园套餐、1项目套餐
     */
    private Integer level;

    /**
     * level=1时，此字段记录项目的id
     */
    private String gameid;

    /**
     * 套餐id
     */
    private String couponid;

    private static final long serialVersionUID = 1L;

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Integer[] getCanbuygoodstypes() {
        return canbuygoodstypes;
    }

    public void setCanbuygoodstypes(Integer[] canbuygoodstypes) {
        this.canbuygoodstypes = canbuygoodstypes;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
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
        UserCouponDo other = (UserCouponDo) that;
        return (this.getObjectid() == null ? other.getObjectid() == null : this.getObjectid().equals(other.getObjectid()))
                && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
                && (this.getAmusementparkid() == null ? other.getAmusementparkid() == null : this.getAmusementparkid().equals(other.getAmusementparkid()))
                && (this.getParkname() == null ? other.getParkname() == null : this.getParkname().equals(other.getParkname()))
                && (this.getOpenid() == null ? other.getOpenid() == null : this.getOpenid().equals(other.getOpenid()))
                && (this.getTimezone() == null ? other.getTimezone() == null : this.getTimezone().equals(other.getTimezone()))
                && (this.getStartdate() == null ? other.getStartdate() == null : this.getStartdate().equals(other.getStartdate()))
                && (this.getEnddate() == null ? other.getEnddate() == null : this.getEnddate().equals(other.getEnddate()))
                && (this.getUpdatetime() == null ? other.getUpdatetime() == null : this.getUpdatetime().equals(other.getUpdatetime()))
                && (this.getOrderid() == null ? other.getOrderid() == null : this.getOrderid().equals(other.getOrderid()))
                && (this.getCanbuygoodstypes() == null ? other.getCanbuygoodstypes() == null : this.getCanbuygoodstypes().equals(other.getCanbuygoodstypes()))
                && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
                && (this.getGameid() == null ? other.getGameid() == null : this.getGameid().equals(other.getGameid()))
                && (this.getCouponid() == null ? other.getCouponid() == null : this.getCouponid().equals(other.getCouponid()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getObjectid() == null) ? 0 : getObjectid().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getAmusementparkid() == null) ? 0 : getAmusementparkid().hashCode());
        result = prime * result + ((getParkname() == null) ? 0 : getParkname().hashCode());
        result = prime * result + ((getOpenid() == null) ? 0 : getOpenid().hashCode());
        result = prime * result + ((getTimezone() == null) ? 0 : getTimezone().hashCode());
        result = prime * result + ((getStartdate() == null) ? 0 : getStartdate().hashCode());
        result = prime * result + ((getEnddate() == null) ? 0 : getEnddate().hashCode());
        result = prime * result + ((getUpdatetime() == null) ? 0 : getUpdatetime().hashCode());
        result = prime * result + ((getOrderid() == null) ? 0 : getOrderid().hashCode());
        result = prime * result + ((getCanbuygoodstypes() == null) ? 0 : getCanbuygoodstypes().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getGameid() == null) ? 0 : getGameid().hashCode());
        result = prime * result + ((getCouponid() == null) ? 0 : getCouponid().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", objectid=").append(objectid);
        sb.append(", userid=").append(userid);
        sb.append(", amusementparkid=").append(amusementparkid);
        sb.append(", parkname=").append(parkname);
        sb.append(", openid=").append(openid);
        sb.append(", timezone=").append(timezone);
        sb.append(", startdate=").append(startdate);
        sb.append(", enddate=").append(enddate);
        sb.append(", updatetime=").append(updatetime);
        sb.append(", orderid=").append(orderid);
        sb.append(", canbuygoodstypes=").append(canbuygoodstypes);
        sb.append(", level=").append(level);
        sb.append(", gameid=").append(gameid);
        sb.append(", couponid=").append(couponid);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}