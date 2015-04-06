package com.jfreer.game.ddz.exception;

/**
 * User: landy
 * Date: 15/3/31
 * Time: 下午2:37
 */
public class PlayerNotOnTheTableException extends DDZException {
    private final String player;
    private final Integer table;

    public PlayerNotOnTheTableException(String player, Integer table) {
        super(player + " not one the table " + table);
        this.player = player;
        this.table = table;
    }

    public String getPlayer() {
        return player;
    }

    public Integer getTable() {
        return table;
    }
}
