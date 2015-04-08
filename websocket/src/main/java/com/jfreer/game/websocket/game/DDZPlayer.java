package com.jfreer.game.websocket.game;

import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.websocket.handler.DDZSession;
import com.jfreer.game.websocket.protocol.IResp;
import gen.response.*;

import java.io.IOException;

/**
 * User: landy
 * Date: 15/3/30
 * Time: 下午1:12
 */
public class DDZPlayer extends Player {
    private DDZSession session;

    public DDZPlayer(Integer playerId) {
        super(playerId);
    }

    public void setSession(DDZSession session) {
        this.session = session;
    }

    public DDZSession getSession() {
        return session;
    }


    @Override
    public void afterTableFull(Table table) {

    }

    public void afterJoinTable(Table table) {
        super.afterJoinTable(table);
        try {
            int pos = table.getPlayerPos(this);
            JoinTableResp resp = new JoinTableResp();
            resp.setPid(this.getPlayerId());
            resp.setTableId(table.getTableId());
            resp.setTablePos(pos);
            pushToClient(resp);
        } catch (PlayerNotOnTheTableException e) {
            e.printStackTrace();
        }
    }

    public void pushToClient(IResp resp) {
        try {
            getSession().pushToClient(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyBeginPlay(int currentPos) {
        PublishCardsResp resp = new PublishCardsResp();
        resp.setPid(getPlayerId());
        int[] rst = new int[getHandCards().size()];
        int i = 0;
        for (Byte c : getHandCards()) {
            rst[i++] = c;
        }
        resp.setCards(rst);
        resp.setNextTablePos(currentPos);
        pushToClient(resp);
    }

    @Override
    public void notifyCallDealer(int tablePos, int nextTablePos, int callFlag) {
        CallDealerResp resp = new CallDealerResp();
        resp.setPid(getPlayerId());
        resp.setIsCall(callFlag);
        resp.setTablePos(tablePos);
        resp.setNextTablePos(nextTablePos);
        pushToClient(resp);

    }

    @Override
    public void notifyBelowCards(int dealerPos, byte[] belowCards) {
        PublishBelowCardsResp resp = new PublishBelowCardsResp();
        resp.setBelowCards(toInt(belowCards));
        resp.setDealerTablePos(dealerPos);
        pushToClient(resp);
    }

    @Override
    public void notifyPlayedCards(int tablePos, int nextTablePos, byte[] cards, HistoryCards lastCards) {
        PlayCardsResp resp = new PlayCardsResp();
        resp.setTablePos(tablePos);
        resp.setNextTablePos(nextTablePos);
        resp.setGiveUp(cards == null ? 1 : 0);
        resp.setCards(toInt(cards));
        pushToClient(resp);
    }

    private int[] toInt(byte[] cards) {
        if (cards == null) {
            return new int[0];
        }
        int[] rst = new int[cards.length];
        for (int i = 0; i < cards.length; i++) {
            rst[i] = cards[i];
        }
        return rst;
    }
}
