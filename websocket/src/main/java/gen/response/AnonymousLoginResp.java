package gen.response;

import com.jfreer.game.websocket.IResp;

/**
* 匿名登陆
*/
public class AnonymousLoginResp extends IResp {

    /** 服务器分配的匿名id */
    private long uid;

    public AnonymousLoginResp() {
        super(0);
    }

    public void setUid(long uid){
        this.uid=uid;
    }

    public long getUid(){
        return this.uid;
    }

}
