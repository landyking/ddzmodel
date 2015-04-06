package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 加入桌子
*/
public class JoinTableResp extends IResp {

    /** 桌号 */
    private int tableId;

    /** 位置 */
    private int tablePos;

    /** 玩家id */
    private int pid;

    public JoinTableResp() {
        super(1);
    }

    public void setTableId(int tableId){
        this.tableId=tableId;
    }

    public int getTableId(){
        return this.tableId;
    }

    public void setTablePos(int tablePos){
        this.tablePos=tablePos;
    }

    public int getTablePos(){
        return this.tablePos;
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

}
