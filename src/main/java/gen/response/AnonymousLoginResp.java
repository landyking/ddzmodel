package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 匿名登陆
*/
public class AnonymousLoginResp extends IResp {

    /** 服务器分配的匿名id */
    private int pid;

    public AnonymousLoginResp() {
        super(0);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
