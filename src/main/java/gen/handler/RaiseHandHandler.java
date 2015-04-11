package gen.handler;

import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.websocket.game.DDZPlayer;
import com.jfreer.game.websocket.game.PlayerManager;
import com.jfreer.game.websocket.protocol.IProtocol;
import gen.request.RaiseHandReq;
import gen.response.RaiseHandResp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* 举手
*/
@Component
@Scope("prototype")
public class RaiseHandHandler extends IProtocol<RaiseHandReq> {
    @Resource
    private TableManager tableManager;
    @Resource
    private PlayerManager playerManager;
    @Override
    public void process(RaiseHandReq req) throws Exception {
        DDZPlayer player = playerManager.getPlayerByPid(req.getPid());
        tableManager.raiseHands(player, player.getCurrentTable().getTableId());
    }
}