package com.jfreer.game.ddz;

/**
 * Created by landy on 2015/3/16.
 */
public class RobotPlayer extends Player {
    private PlayedCards lastHistory;

    @Override
    public void turnToPlay(byte oldOrderNo) {

    }

    public void notifyPlayedCards(PlayedCards history) {
        this.lastHistory=history;
    }
}
