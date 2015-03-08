package com.jfreer.game.ddz;

/**
 * Created by landy on 2015/3/7.
 */
public class RobotPlayer extends Player{
    public RobotPlayer(Integer playerId) {
        super(playerId);
    }

    @Override
    public String toString() {
        return "Robot:"+this.getPlayerId();
    }
}
