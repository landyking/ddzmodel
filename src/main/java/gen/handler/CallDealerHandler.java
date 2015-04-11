package gen.handler;

import com.jfreer.game.websocket.protocol.IProtocol;
import gen.request.CallDealerReq;
//import gen.response.CallDealerResp;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* 叫地主
*/
@Component
@Scope("prototype")
public class CallDealerHandler extends IProtocol<CallDealerReq> {

    @Override
    public void process(CallDealerReq req) throws Exception {
        //TODO
    }
}