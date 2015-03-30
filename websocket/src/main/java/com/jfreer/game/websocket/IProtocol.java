package com.jfreer.game.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:41
 */
public abstract class IProtocol<T extends IReq> {
    private WebSocketSession session;

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public abstract void process(T req) throws Exception;

    protected void pushToClient(IResp resp) throws IOException {
        MsgStruct struct = new MsgStruct();
        struct.setNo(resp.getNo());
        struct.setData(JsonUtils.toJson(resp));
        getSession().sendMessage(new TextMessage(JsonUtils.toJson(struct)));
    }
}
