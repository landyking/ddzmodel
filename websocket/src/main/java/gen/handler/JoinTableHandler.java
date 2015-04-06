package gen.handler;

import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.websocket.game.DDZPlayer;
import com.jfreer.game.websocket.game.PlayerManager;
import com.jfreer.game.websocket.protocol.IProtocol;
import gen.request.JoinTableReq;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* 加入桌子
*/
@Component
@Scope("prototype")
public class JoinTableHandler extends IProtocol<JoinTableReq> {
    @Resource
    private TableManager tableManager;
    @Override
    public void process(JoinTableReq req) throws Exception {
        DDZPlayer player = PlayerManager.getPlayerByPid(req.getPid());
        tableManager.joinTable(player,req.getTableId());
    }
}