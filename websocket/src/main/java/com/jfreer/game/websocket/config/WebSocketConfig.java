package com.jfreer.game.websocket.config;

import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.core.share.TableManagerForShareQueueTable;
import com.jfreer.game.ddz.thread.ProcessManager;
import com.jfreer.game.websocket.handler.WebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
@ComponentScan(basePackages = {"com.jfreer.game","gen.handler"})
@EnableWebMvc
public class WebSocketConfig implements WebSocketConfigurer {
    @Resource
    private WebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ddzws").withSockJS();
    }

    @Bean
    @Lazy
    public TableManager tableManager(){
        ProcessManager processManager = new ProcessManager(3);
        TableManager manager = new TableManagerForShareQueueTable(processManager);
        return manager;
    }
}