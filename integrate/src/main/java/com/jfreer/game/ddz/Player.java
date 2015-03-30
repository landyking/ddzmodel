package com.jfreer.game.ddz;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/3/7.
 */
public class Player {
    private static Random rd = new Random();
    private Integer playerId;
    private Integer currentTableId;

    public Player(Integer playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Player:" + playerId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        Player player = (Player) o;

        if (playerId != null ? !playerId.equals(player.playerId) : player.playerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return playerId != null ? playerId.hashCode() : 0;
    }

    public void setCurrentTableId(Integer currentTableId) {
        this.currentTableId = currentTableId;
    }

    public Integer getCurrentTableId() {
        return currentTableId;
    }

    public void notifyCallDealer(Table table, byte orderNo) {
        try {
//            TimeUnit.SECONDS.sleep(30);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        table.callDealer(this, rd.nextBoolean(), orderNo);
    }

    public void setHandCards(byte[] card) {

    }
}
