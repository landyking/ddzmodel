package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 发牌
*/
public class PublishCardsResp extends IResp {

    /** 用户id */
    private int pid;

    /** 手牌 */
    private int[] cards;

    public PublishCardsResp() {
        super(3);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

    public void setCards(int[] cards){
        this.cards=cards;
    }

    public int[] getCards(){
        return this.cards;
    }

}
