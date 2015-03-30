package gen.request;

import com.jfreer.game.websocket.IReq;

/**
* 加入桌子
*/
public class JoinTableReq extends IReq {

    /** 用户id */
    private long uid;

    public JoinTableReq() {
        super(1);
    }

    public void setUid(long uid){
        this.uid=uid;
    }

    public long getUid(){
        return this.uid;
    }

}
