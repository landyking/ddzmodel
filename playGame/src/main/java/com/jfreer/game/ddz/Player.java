package com.jfreer.game.ddz;

import java.util.*;

/**
 * User: landy
 * Date: 15/3/13
 * Time: 上午11:39
 */
public class Player {
    public Player(int playerId) {
        this.playerId = playerId;
    }

    protected static final Random random = new Random();
    private LinkedList<Byte> handCards = new LinkedList<Byte>();
    private int playerId;


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

    public void turnToPlay(Table table, byte oldOrderNo) {

    }

    public void notifyPlayedCards(PlayedCards history) {
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Player:" + playerId;
    }
}
