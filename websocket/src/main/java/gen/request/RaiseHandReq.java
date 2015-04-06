package gen.request;

import com.jfreer.game.websocket.protocol.IReq;

/**
* 举手
*/
public class RaiseHandReq extends IReq {

    /** 用户id */
    private int pid;

    public RaiseHandReq() {
        super(2);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
