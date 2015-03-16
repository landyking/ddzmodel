package com.jfreer.game.ddz;

/**
 * User: landy
 * Date: 15/3/16
 * Time: 下午5:29
 */
public class PlayedCards {
    private int position;
    private byte[] cards;

    public PlayedCards() {
    }

    public PlayedCards(int position, byte[] cards) {
        this.position = position;
        this.cards = cards;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public byte[] getCards() {
        return cards;
    }

    public void setCards(byte[] cards) {
        this.cards = cards;
    }
}
