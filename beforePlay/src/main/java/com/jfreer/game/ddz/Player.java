package com.jfreer.game.ddz;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * User: landy
 * Date: 15/3/9
 * Time: 上午11:36
 */
public class Player {
    private static Random rd = new Random();
    private int playerId;

    public Player(int playerId) {
        this.playerId = playerId;
    }


    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setHandCards(byte[] card) {

    }

    @Override
    public String toString() {
        return "Player:" + playerId;
    }


    public void notifyCallDealer(Table table) {
        try {
//            TimeUnit.SECONDS.sleep(30);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        table.callDealer(this, rd.nextBoolean());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (playerId != player.playerId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return playerId;
    }
}
