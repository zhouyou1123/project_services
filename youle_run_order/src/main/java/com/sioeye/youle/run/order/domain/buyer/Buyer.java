package com.sioeye.youle.run.order.domain.buyer;

import com.sioeye.youle.run.order.domain.common.AbstractId;
import lombok.Getter;

@Getter
public class Buyer extends AbstractId {

    private String openId;
    private String userName;
    public Buyer(String id,String openId)
    {
        super(id);
        setOpenId(openId);
    }
    private void setOpenId(String openId){
        this.openId = openId;
    }
}
