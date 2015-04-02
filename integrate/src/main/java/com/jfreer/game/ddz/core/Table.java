package com.jfreer.game.ddz.core;

import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.player.RobotPlayer;

/**
 * Created by landy on 2015/4/2.
 */
public abstract class Table {

    public abstract Integer getTableId();

    public abstract boolean callDealer(Player player, boolean b, byte orderNo);
    public abstract boolean playCards(Player player, byte[] cards, byte orderNo);
    public abstract boolean playNothing(Player player, byte orderNo);
}
