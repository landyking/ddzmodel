package gen.handler;

import com.jfreer.game.websocket.protocol.IProtocol;
import gen.request.PlayCardsReq;
//import gen.response.PlayCardsResp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* 出牌
*/
@Component
@Scope("prototype")
public class PlayCardsHandler extends IProtocol<PlayCardsReq> {

    @Override
    public void process(PlayCardsReq req) throws Exception {
        //TODO
    }
}