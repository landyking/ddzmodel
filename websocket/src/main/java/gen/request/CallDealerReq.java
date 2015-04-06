package gen.request;

import com.jfreer.game.websocket.protocol.IReq;

/**
* 叫地主
*/
public class CallDealerReq extends IReq {

    /** player id */
    private int pid;

    /** 1:call,0:give up */
    private byte isCall;

    public CallDealerReq() {
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
