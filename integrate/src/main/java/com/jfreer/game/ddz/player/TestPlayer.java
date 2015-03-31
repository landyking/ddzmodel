package com.jfreer.game.ddz.player;

import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.core.TableManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/3/7.
 */
public class TestPlayer extends Player {


    private final TableManager tableManager;

    public TestPlayer(Integer playerId, TableManager tableManager) {
        super(playerId);
        this.tableManager = tableManager;
    }

    public void notifyCallDealer(Table table, byte orderNo) {
        try {
//            TimeUnit.SECONDS.sleep(30);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        table.callDealer(this, random.nextBoolean(), orderNo);
    }


    public void turnToPlay(Table table, byte oldOrderNo, HistoryCards lastHistory) {

    }

    public void notifyPlayedCards(HistoryCards history) {
    }


    public void afterJoinTable(Table table) {
        super.afterJoinTable(table);
        tableManager.raiseHands(this, table.getTableId());
    }

    public void afterTableFull(Table table) {

    }

    @Override
    public void afterGameOver(Table table) {
        super.afterGameOver(table);
        tableManager.raiseHands(this, table.getTableId());
    }
}
