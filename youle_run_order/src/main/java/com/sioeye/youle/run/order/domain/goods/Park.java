package com.sioeye.youle.run.order.domain.goods;

import com.sioeye.youle.run.order.domain.common.AbstractId;

public class Park extends AbstractId {

    private String parkName;
    public Park(String parkId,String parkName){
        super(parkId);
        setParkName(parkName);
    }
    private void setParkName(String parkName){
        this.assertArgumentNotEmpty(parkName,"the park name is not empty.");
        this.parkName = parkName;
    }
    public String getParkName(){
        return parkName;
    }
}
