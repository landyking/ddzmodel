package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 叫地主
*/
public class CallDealerResp extends IResp {

    /** 用户id */
    private int pid;

    /** 1:call,0:give up */
    private byte isCall;

    public CallDealerResp() {
        super(5);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

    public void setIsCall(byte isCall){
        this.isCall=isCall;
    }

    public byte getIsCall(){
        return this.isCall;
    }

}
