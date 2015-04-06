package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 举手
*/
public class RaiseHandResp extends IResp {

    /** 用户id */
    private int pid;

    public RaiseHandResp() {
        super(2);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
