package com.jfreer.game.websocket.game;

import com.google.common.collect.Maps;
import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.websocket.handler.DDZSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentMap;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 下午12:07
 */
@Service
public class PlayerManager {
    @Resource
    private TableManager tableManager;
    private final ConcurrentMap<Integer, DDZPlayer> playerMap = Maps.newConcurrentMap();
    private final ConcurrentMap<String, DDZPlayer> session2Player = Maps.newConcurrentMap();

    public  boolean containPlayerByPid(int pid) {
        return playerMap.containsKey(pid);
    }

    public  boolean containPlayerBySid(String sessionId) {
        return session2Player.containsKey(sessionId);
    }

    public  DDZPlayer getPlayerByPid(int pid) {
        return playerMap.get(pid);
    }

    public  DDZPlayer getPlayerBySid(String sessionId) {
        return session2Player.get(sessionId);
    }

    public  DDZPlayer newPlayer(DDZSession session) {
        DDZPlayer player = new DDZPlayer(Ids.newPlayerId(),tableManager);
        player.setSession(session);
        if (playerMap.putIfAbsent(player.getPlayerId(), player) == null
                &&
                session2Player.putIfAbsent(session.getId(), player) == null) {
            return player;
        }
        throw new RuntimeException("new player failure!");
    }
}
