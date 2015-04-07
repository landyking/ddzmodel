package com.jfreer.game.websocket.config;

import com.google.common.base.Strings;
import com.jfreer.game.ddz.core.TableManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by landy on 2015/4/6.
 */
@Service
public class StartGameServer implements ApplicationListener<ContextRefreshedEvent>{


    public static final Log LOG = LogFactory.getLog(StartGameServer.class);
    private boolean init=false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!init){
            init=true;
            TableManager manager = event.getApplicationContext().getBean(TableManager.class);
            manager.start();
            int count = 25;
            LOG.info(Strings.repeat("#", count));
            LOG.info("Game Server started !!!");
            LOG.info(Strings.repeat("#", count));
        }
    }
}
