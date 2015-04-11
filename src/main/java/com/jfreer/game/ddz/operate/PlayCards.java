package com.jfreer.game.ddz.operate;

/**
 * User: landy
 * Date: 15/3/16
 * Time: 下午5:06
 */
public class PlayCards extends TableUserOperate {
    private byte[] cards;

    public byte[] getCards() {
        return cards;
    }

    public void setCards(byte[] cards) {
        this.cards = cards;
    }
    public boolean hasCards(){
        return this.cards!=null&&this.cards.length>0;
    }
}
