package com.jfreer.game.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfreer.game.websocket.protocol.IProtocol;
import com.jfreer.game.websocket.protocol.IReq;
import gen.ProtocolUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler implements BeanFactoryAware{
    private final static Log LOG = LogFactory.getLog(WebSocketHandler.class);
    private ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<String, WebSocketSession>();
    private BeanFactory factory;

    public ConcurrentHashMap<String, WebSocketSession> getSessionMap() {
        return sessionMap;
    }

    private ObjectMapper json = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(session.getId() + ":" + message.getPayload());
        //System.out.println(message.getPayload());
        try {
            MsgStruct msgStruct = json.readValue(message.getPayload(), MsgStruct.class);
            Integer protocolNo = msgStruct.getNo();
            if (ProtocolUtils.REQ_MAPPINGS.containsKey(protocolNo)) {
                ProtocolUtils.Tuple tuple = ProtocolUtils.REQ_MAPPINGS.get(protocolNo);
                Class reqClazz = tuple.getReqClass();
                Class protocolClazz = tuple.getHandlerClass();
                Object obj = json.readValue(msgStruct.getData(), reqClazz);
                if (obj instanceof IReq) {
                    IProtocol protocol = (IProtocol) this.factory.getBean(protocolClazz);
                    protocol.setSession(new DDZSession(session));
                    protocol.process((IReq) obj);
                } else {
                    LOG.error(String.format("[%s] must extend [%s]!", protocolClazz.toString(), IProtocol.class.toString()));
                }
            } else {
                LOG.error("Protocol number not exist !!!" + protocolNo);
            }
        } catch (Exception e) {
            LOG.error("处理接收到的消息出错！"+e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("session:" + session.toString() + ":" + status.toString());
        sessionMap.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("session:" + session.toString() + "#####");
        sessionMap.put(session.getId(), session);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.factory=beanFactory;
    }
}