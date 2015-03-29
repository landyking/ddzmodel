package gen.handler;

import com.jfreer.game.websocket.IProtocol;
import gen.request.ReqPlayCard;
import org.springframework.web.socket.WebSocketSession;

/**
 * User: landy
 * Date: 15/3/27
 * Time: 下午5:43
 */
public class PlayCardHandler extends IProtocol<ReqPlayCard> {
    @Override
    public void process(WebSocketSession session, ReqPlayCard req) {
        System.out.println("session:"+session.getId()+":"+req.toString());
    }
}
