package gen.request;

import com.jfreer.game.websocket.IReq;

/**
* 匿名登陆
*/
public class AnonymousLoginReq extends IReq {

    /** 旧的uid,没有则为-1 */
    private long uid;

    public AnonymousLoginReq() {
        super(0);
    }

    public void setUid(long uid){
        this.uid=uid;
    }

    public long getUid(){
        return this.uid;
    }

}
