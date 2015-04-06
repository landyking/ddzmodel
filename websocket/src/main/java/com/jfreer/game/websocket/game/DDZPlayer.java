package com.jfreer.game.websocket.game;

import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Log;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.websocket.handler.DDZSession;
import gen.response.JoinTableResp;

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

    private void pushToClient(JoinTableResp resp) {
        try {
            getSession().pushToClient(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
