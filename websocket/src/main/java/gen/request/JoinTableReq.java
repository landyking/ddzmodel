package gen.request;

import com.jfreer.game.websocket.protocol.IReq;

/**
* 加入桌子
*/
public class JoinTableReq extends IReq {

    /** 桌号，默认-1 */
    private int tableId;

    /** 玩家id */
    private int pid;

    public JoinTableReq() {
        super(1);
    }

    public void setTableId(int tableId){
        this.tableId=tableId;
    }

    public int getTableId(){
        return this.tableId;
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
