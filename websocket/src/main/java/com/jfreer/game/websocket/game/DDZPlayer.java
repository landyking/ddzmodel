package com.jfreer.game.websocket.game;

import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Log;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.websocket.handler.DDZSession;
import com.jfreer.game.websocket.protocol.IResp;
import gen.response.CallDealerResp;
import gen.response.JoinTableResp;
import gen.response.NotifyCallDealerResp;
import gen.response.PublishCardsResp;

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
    public void notifyCallDealer(Table table, byte orderNo) {
        try {
            NotifyCallDealerResp resp = new NotifyCallDealerResp();
            resp.setPid(getPlayerId());
            resp.setTablePos(table.getPlayerPos(this));
            pushToSameTableAllPlayer(resp);
        } catch (PlayerNotOnTheTableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void turnToPlay(Table table, byte oldOrderNo, HistoryCards lastHistory) {

    }

    @Override
    public void notifyPlayedCards(HistoryCards history) {

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

    public void publishHandCards(byte[] card){
        super.publishHandCards(card);
        PublishCardsResp resp = new PublishCardsResp();
        resp.setPid(getPlayerId());
        int[] rst = new int[card.length];
        for (int i = 0; i < card.length; i++) {
            rst[i] = card[i];
        }
        resp.setCards(rst);
        pushToClient(resp);
    }
    public void pushToClient(IResp resp) {
        try {
            getSession().pushToClient(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterCallDealer(boolean call) {
        try {
            CallDealerResp resp = new CallDealerResp();
            resp.setPid(getPlayerId());
            resp.setIsCall(call ? 1 : 0);
            resp.setTablePos(getCurrentTable().getPlayerPos(this));
            pushToSameTableAllPlayer(resp);
        } catch (PlayerNotOnTheTableException e) {
            e.printStackTrace();
        }
    }
    public void pushToSameTableAllPlayer(final IResp resp){
        getCurrentTable().eachPlayer(new Table.ApplyPlayer() {
            @Override
            public void apply(Player one) {
                one.pushToClient(resp);
            }
        });
    }
}
