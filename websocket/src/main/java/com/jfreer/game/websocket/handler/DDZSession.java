package com.jfreer.game.websocket.handler;

import com.jfreer.game.websocket.util.JsonUtils;
import com.jfreer.game.websocket.protocol.IResp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by landy on 2015/4/6.
 */
public class DDZSession {

    public static final Log LOG = LogFactory.getLog(DDZSession.class);
    private WebSocketSession session;

    public DDZSession(WebSocketSession session) {
        this.session = session;
    }

    public void pushToClient(IResp resp) throws IOException {
        MsgStruct struct = new MsgStruct();
        struct.setNo(resp.getNo());
        struct.setData(JsonUtils.toJson(resp));
        this.session.sendMessage(new TextMessage(JsonUtils.toJson(struct)));
    }

    public String getId() {
        return session.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DDZSession that = (DDZSession) o;

        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return session != null ? session.getId().hashCode() : 0;
    }

    public void logout() {
        try {
            this.session.close();
        } catch (IOException e) {
            LOG.error("close session error!" + e.getMessage(), e);
        }
    }
}
