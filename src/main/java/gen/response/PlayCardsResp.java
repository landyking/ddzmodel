package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 出牌
*/
public class PlayCardsResp extends IResp {

    /** player tablePos */
    private int tablePos;

    /** next player tablePos */
    private int nextTablePos;

    /** 1:give up,0:not */
    private int giveUp;

    /** played cards */
    private int[] cards;

    public PlayCardsResp() {
        super(7);
    }

    public void setTablePos(int tablePos){
        this.tablePos=tablePos;
    }

    public int getTablePos(){
        return this.tablePos;
    }

    public void setNextTablePos(int nextTablePos){
        this.nextTablePos=nextTablePos;
    }

    public int getNextTablePos(){
        return this.nextTablePos;
    }

    public void setGiveUp(int giveUp){
        this.giveUp=giveUp;
    }

    public int getGiveUp(){
        return this.giveUp;
    }

    public void setCards(int[] cards){
        this.cards=cards;
    }

    public int[] getCards(){
        return this.cards;
    }

}
