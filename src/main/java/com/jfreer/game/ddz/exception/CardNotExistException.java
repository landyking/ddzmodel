package com.jfreer.game.ddz.exception;

/**
 * User: landy
 * Date: 15/3/31
 * Time: 下午3:52
 */
public class CardNotExistException extends DDZException {
    private String player;
    private Byte card;

    public CardNotExistException(String player, Byte card) {
        super(String.format(player + " hasn't Card [%s]", card));
        this.player = player;
        this.card = card;
    }

    public String getPlayer() {
        return player;
    }

    public Byte getCard() {
        return card;
    }
}
