package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 叫地主
*/
public class CallDealerResp extends IResp {

    /** 用户id */
    private int pid;

    /** 桌子位置 */
    private int tablePos;

    /** 1:call,0:give up */
    private int isCall;

    public CallDealerResp() {
        super(5);
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

    public void setIsCall(int isCall){
        this.isCall=isCall;
    }

    public int getIsCall(){
        return this.isCall;
    }

}
