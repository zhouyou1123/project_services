package com.sioeye.youle.run.order.gateways.repository.dataobject;

import java.io.Serializable;

/**
 * orderitemextend
 * @author 
 */
public class OrderItemExtendDo implements Serializable {
    private String objectid;

    private String orderid;

    /**
     * 商品扩展json数据
     */
    private String extendcontext;
    /**
     * 订单项扩展json数据
     */
    private String itemextendcontext;

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

    public String getItemextendcontext() {
        return itemextendcontext;
    }

    public void setItemextendcontext(String itemextendcontext) {
        this.itemextendcontext = itemextendcontext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItemExtendDo that = (OrderItemExtendDo) o;

        if (objectid != null ? !objectid.equals(that.objectid) : that.objectid != null) return false;
        if (orderid != null ? !orderid.equals(that.orderid) : that.orderid != null) return false;
        if (extendcontext != null ? !extendcontext.equals(that.extendcontext) : that.extendcontext != null)
            return false;
        return itemextendcontext != null ? itemextendcontext.equals(that.itemextendcontext) : that.itemextendcontext == null;

    }

    @Override
    public int hashCode() {
        int result = objectid != null ? objectid.hashCode() : 0;
        result = 31 * result + (orderid != null ? orderid.hashCode() : 0);
        result = 31 * result + (extendcontext != null ? extendcontext.hashCode() : 0);
        result = 31 * result + (itemextendcontext != null ? itemextendcontext.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderItemExtendDo{" +
                "objectid='" + objectid + '\'' +
                ", orderid='" + orderid + '\'' +
                ", extendcontext='" + extendcontext + '\'' +
                ", itemextendcontext='" + itemextendcontext + '\'' +
                '}';
    }
}