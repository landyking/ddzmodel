package gen.handler;

import com.jfreer.game.websocket.game.DDZPlayer;
import com.jfreer.game.websocket.protocol.IProtocol;
import com.jfreer.game.websocket.game.PlayerManager;
import gen.request.AnonymousLoginReq;
import gen.response.AnonymousLoginResp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 上午11:38
 */
@Component
@Scope("prototype")
public class AnonymousLoginHandler extends IProtocol<AnonymousLoginReq> {
    @Resource
    private PlayerManager playerManager;
    @Override
    public void process(AnonymousLoginReq req) throws Exception {
        DDZPlayer player=null;
        if (req.getPid() != -1) {
            if(playerManager.containPlayerByPid(req.getPid())) {
                player = playerManager.getPlayerByPid(req.getPid());
                if (!getSession().equals(player.getSession())) {
                    player.getSession().logout();
                    player.setSession(getSession());
                }
            }
        }else {
            player = playerManager.newPlayer(getSession());
        }
        returnResult(player.getPlayerId());
    }
    private void returnResult(int newId) throws java.io.IOException {
        AnonymousLoginResp resp = new AnonymousLoginResp();
        resp.setPid(newId);
        getSession().pushToClient(resp);
    }
}
