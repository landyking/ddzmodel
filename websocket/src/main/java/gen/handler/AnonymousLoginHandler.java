package gen.handler;

import com.jfreer.game.websocket.IProtocol;
import com.jfreer.game.websocket.Ids;
import com.jfreer.game.websocket.PlayerManager;
import gen.request.AnonymousLoginReq;
import gen.response.AnonymousLoginResp;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 上午11:38
 */
public class AnonymousLoginHandler extends IProtocol<AnonymousLoginReq> {

    @Override
    public void process(AnonymousLoginReq req) throws Exception {
        if (req.getUid() == -1) {
            returnResult(generateNewUid());
        } else {
            if (inEffect(req.getUid())) {
                returnResult(req.getUid());
            } else {
                returnResult(generateNewUid());
            }
        }
    }

    private boolean inEffect(long uid) {
        return PlayerManager.containPlayer(uid);
    }

    private void returnResult(long newId) throws java.io.IOException {
        AnonymousLoginResp resp = new AnonymousLoginResp();
        resp.setUid(newId);
        pushToClient(resp);
    }

    private long generateNewUid() {
        return Ids.newPlayerId();
    }
}
