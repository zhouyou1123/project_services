package com.sioeye.youle.run.order.model;

import java.io.Serializable;

/**
 * orderitemextend
 * @author 
 */
public class OrderItemExtend implements Serializable {
    private String objectid;

    private String orderid;

    /**
     * json格式
     */
    private String extendcontext;

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

    public String getExtendcontext() {
        return extendcontext;
    }

    public void setExtendcontext(String extendcontext) {
        this.extendcontext = extendcontext;
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
        OrderItemExtend other = (OrderItemExtend) that;
        return (this.getObjectid() == null ? other.getObjectid() == null : this.getObjectid().equals(other.getObjectid()))
            && (this.getOrderid() == null ? other.getOrderid() == null : this.getOrderid().equals(other.getOrderid()))
            && (this.getExtendcontext() == null ? other.getExtendcontext() == null : this.getExtendcontext().equals(other.getExtendcontext()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getObjectid() == null) ? 0 : getObjectid().hashCode());
        result = prime * result + ((getOrderid() == null) ? 0 : getOrderid().hashCode());
        result = prime * result + ((getExtendcontext() == null) ? 0 : getExtendcontext().hashCode());
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
        sb.append(", extendcontext=").append(extendcontext);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}