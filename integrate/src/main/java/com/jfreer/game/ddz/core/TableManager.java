package com.jfreer.game.ddz.core;

import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.player.RobotPlayer;

/**
 * Created by landy on 2015/4/2.
 */
public abstract class TableManager {
    public abstract void raiseHands(Player player, Integer tableId);
    public abstract void joinTable(Player player, Integer tableId);
}
