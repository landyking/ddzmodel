package gen.request;

import com.jfreer.game.websocket.protocol.IReq;

/**
* 出牌
*/
public class PlayCardsReq extends IReq {

    /** player id */
    private int pid;

    /** 1:give up,0:not */
    private int giveUp;

    /** played cards */
    private byte[] cards;

    public PlayCardsReq() {
        super(7);
    }

    public void setPid(int pid){
        this.pid=pid;
    }

    public int getPid(){
        return this.pid;
    }

    public void setGiveUp(int giveUp){
        this.giveUp=giveUp;
    }

    public int getGiveUp(){
        return this.giveUp;
    }

    public void setCards(byte[] cards){
        this.cards=cards;
    }

    public byte[] getCards(){
        return this.cards;
    }

}
