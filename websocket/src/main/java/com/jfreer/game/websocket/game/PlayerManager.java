package com.jfreer.game.websocket.game;

import com.google.common.collect.Maps;
import com.jfreer.game.websocket.util.Ids;
import com.jfreer.game.websocket.handler.DDZSession;

import java.util.concurrent.ConcurrentMap;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 下午12:07
 */
public class PlayerManager {
    private static final ConcurrentMap<Integer, DDZPlayer> playerMap = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, DDZPlayer> session2Player = Maps.newConcurrentMap();

    public static boolean containPlayerByPid(int pid) {
        return playerMap.containsKey(pid);
    }

    public static boolean containPlayerBySid(String sessionId) {
        return session2Player.containsKey(sessionId);
    }

    public static DDZPlayer getPlayerByPid(int pid) {
        return playerMap.get(pid);
    }

    public static DDZPlayer getPlayerBySid(String sessionId) {
        return session2Player.get(sessionId);
    }

    public static DDZPlayer newPlayer(DDZSession session) {
        DDZPlayer player = new DDZPlayer(Ids.newPlayerId());
        player.setSession(session);
        if (playerMap.putIfAbsent(player.getPlayerId(), player) == null
                &&
                session2Player.putIfAbsent(session.getId(), player) == null) {
            return player;
        }
        throw new RuntimeException("new player failure!");
    }
}
