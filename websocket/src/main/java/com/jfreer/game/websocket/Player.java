package com.jfreer.game.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 下午1:12
 */
public class Player {
    private long uid;
    private WebSocketSession session;

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
