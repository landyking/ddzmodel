package gen.request;

import com.jfreer.game.websocket.protocol.IReq;

/**
* 匿名登陆
*/
public class AnonymousLoginReq extends IReq {

    /** 旧的pid,没有则为-1 */
    private int pid;

    public AnonymousLoginReq() {
        super(0);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
