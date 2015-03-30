package com.jfreer.game.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 下午12:07
 */
public class PlayerManager {
    private static final ConcurrentMap<Long, Player> playerMap = new ConcurrentHashMap<Long, Player>();

    public static boolean containPlayer(long uid) {
        return playerMap.containsKey(uid);
    }
}
