package com.sioeye.youle.run.order.domain.goods;

import com.sioeye.youle.run.order.domain.common.AbstractId;

public class Game extends AbstractId {

    private String gameName;
    public Game(String gameId,String gameName){
        super(gameId);
        setGameName(gameName);
    }
    private void setGameName(String gameName){
        this.assertArgumentNotEmpty(gameName,"the game name is not empty.");
        this.gameName = gameName;
    }
    public String getGameName(){
        return gameName;
    }

}
