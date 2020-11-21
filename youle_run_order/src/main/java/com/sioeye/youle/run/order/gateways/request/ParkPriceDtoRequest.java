package com.sioeye.youle.run.order.gateways.request;

import com.alibaba.fastjson.annotation.JSONField;

public class ParkPriceDtoRequest {
    private String parkId;
    private String gameId;

    @JSONField(name = "parkId")
    public String getParkId(){
        return parkId;
    }
    @JSONField(name = "gameId")
    public String getGameId(){return gameId;}
    public ParkPriceDtoRequest(String parkId,String gameId){

        this.parkId = parkId;
        this.gameId = gameId;
    }
}
