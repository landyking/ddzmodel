package com.jfreer.game.websocket.mvc;

import com.jfreer.game.websocket.handler.WebSocketHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午3:51
 */
@RestController
public class TestController {
    @Resource
    private WebSocketHandler handler;

    @RequestMapping("say")
    public Collection say(@RequestParam(required = false, value = "text") String text) {
        if (StringUtils.hasText(text)) {
            System.out.println(text);
            for (WebSocketSession session : handler.getSessionMap().values()) {
                try {
                    session.sendMessage(new TextMessage(text));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Collections.singleton(text);
        } else {
            return Collections.singleton("error!");
        }
    }
}
