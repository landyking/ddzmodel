package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 通知叫地主
*/
public class NotifyCallDealerResp extends IResp {

    /** 用户id */
    private int pid;

    /** 桌子位置 */
    private int tablePos;

    public NotifyCallDealerResp() {
        super(4);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

    public void setTablePos(int tablePos){
        this.tablePos=tablePos;
    }

    public int getTablePos(){
        return this.tablePos;
    }

}
