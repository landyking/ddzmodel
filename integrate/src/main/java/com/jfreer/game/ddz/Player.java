package com.jfreer.game.ddz;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/3/7.
 */
public class Player {
    protected static final Random random = new Random();
    private final Integer playerId;
    private Integer currentTableId;
    private LinkedList<Byte> handCards = new LinkedList<Byte>();

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
        table.callDealer(this, random.nextBoolean(), orderNo);
    }

    public void addHandCards(byte[] card) {
        for (byte one : card) {
            this.handCards.add(one);
        }
        Collections.sort(this.handCards);
    }

    public List<Byte> getHandCards() {
        return handCards;
    }

    public boolean hasCards(byte[] cards) {
        for (byte b : cards) {
            if (!handCards.contains(b)) {
                return false;
            }
        }
        return true;
    }

    public void removeCards(byte[] cards) {
        for (Byte b : cards) {
            if (!handCards.remove(b)) {
                throw new RuntimeException("删除手牌出错!" + handCards.toString() + "," + Arrays.toString(cards));
            }
        }
    }

    public void turnToPlay(Table table, byte oldOrderNo, HistoryCards lastHistory) {

    }

    public void notifyPlayedCards(HistoryCards history) {
    }

    public void clear() {
        this.getHandCards().clear();
        this.currentTableId = null;
    }
}
