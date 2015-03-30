package gen.response;

import com.jfreer.game.websocket.IResp;

/**
* 加入桌子
*/
public class JoinTableResp extends IResp {

    /** 桌号 */
    private int tableId;

    /** 位置 */
    private int tablePos;

    /** 其他玩家id */
    private int[] otherUid;

    /** 其他玩家名字 */
    private String[] otherName;

    /** 其他玩家位置 */
    private int[] otherPos;

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

    public void setOtherUid(int[] otherUid){
        this.otherUid=otherUid;
    }

    public int[] getOtherUid(){
        return this.otherUid;
    }

    public void setOtherName(String[] otherName){
        this.otherName=otherName;
    }

    public String[] getOtherName(){
        return this.otherName;
    }

    public void setOtherPos(int[] otherPos){
        this.otherPos=otherPos;
    }

    public int[] getOtherPos(){
        return this.otherPos;
    }

}
