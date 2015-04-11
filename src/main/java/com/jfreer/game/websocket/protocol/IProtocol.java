package com.jfreer.game.websocket.protocol;

import com.jfreer.game.websocket.handler.DDZSession;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:41
 */
public abstract class IProtocol<T extends IReq> {
    private DDZSession session;

    public DDZSession getSession() {
        return session;
    }

    public void setSession(DDZSession session) {
        this.session = session;
    }

    public abstract void process(T req) throws Exception;


}
