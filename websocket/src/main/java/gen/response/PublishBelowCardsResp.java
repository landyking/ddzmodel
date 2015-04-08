package gen.response;

import com.jfreer.game.websocket.protocol.IResp;

/**
* 发底牌
*/
public class PublishBelowCardsResp extends IResp {

    /** 地主位置 */
    private int dealerTablePos;

    /** 底牌 */
    private int[] belowCards;

    public PublishBelowCardsResp() {
        super(6);
    }

    public void setDealerTablePos(int dealerTablePos){
        this.dealerTablePos=dealerTablePos;
    }

    public int getDealerTablePos(){
        return this.dealerTablePos;
    }

    public void setBelowCards(int[] belowCards){
        this.belowCards=belowCards;
    }

    public int[] getBelowCards(){
        return this.belowCards;
    }

}
