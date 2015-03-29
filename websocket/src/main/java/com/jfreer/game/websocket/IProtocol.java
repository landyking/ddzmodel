package com.jfreer.game.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:41
 */
public abstract class IProtocol<T extends IReq> {
    public abstract void process(WebSocketSession session, T req);
}
