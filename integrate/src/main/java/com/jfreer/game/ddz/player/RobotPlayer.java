package com.jfreer.game.ddz.player;

import com.jfreer.game.ddz.CardUtils;
import com.jfreer.game.ddz.DDZThreadPoolExecutor;
import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.core.TableManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/3/7.
 */
public class RobotPlayer extends Player {

    private final TableManager tableManager;

    public RobotPlayer(Integer playerId, TableManager tableManager) {
        super(playerId);
        this.tableManager = tableManager;
    }

    @Override
    public String toString() {
        return "Robot:" + this.getPlayerId();
    }

    @Override
    public void notifyCallDealer(Table table, byte orderNo) {
        table.callDealer(this, random.nextBoolean(), orderNo);
    }

    @Override
    public void turnToPlay(final Table table, final byte oldOrderNo, final HistoryCards lastHistory) {
        /**
         * 出牌时机:为了更像真人,机器人的出牌延迟在5~15秒之间随机
         */
        int delay = random.nextInt(10) + 5;

        DDZThreadPoolExecutor.INSTANCE.schedule(new Runnable() {
            @Override
            public void run() {
                /**
                 * 如果是主导者,则选最小的牌出
                 * 如果是跟出者,则选择最小的但是比上家大的牌出,如果没有,则不出.
                 */
                if (lastHistory == null || getPlayerId().equals(lastHistory.getPlayerId())) {
                    byte[] minCards = CardUtils.getMinCards(getHandCards());
                    table.playCards(RobotPlayer.this, minCards, oldOrderNo);
                } else {
                    byte[] cardsGreaterThan = CardUtils.getCardsGreaterThan(lastHistory.getCards(), getHandCards());
                    if (cardsGreaterThan != null && cardsGreaterThan.length > 0) {
                        table.playCards(RobotPlayer.this, cardsGreaterThan, oldOrderNo);
                    } else {
                        table.playNothing(RobotPlayer.this, oldOrderNo);
                    }
                }
            }
        }, delay, TimeUnit.SECONDS);

    }

    @Override
    public void notifyPlayedCards(HistoryCards history) {

    }

    @Override
    public void afterJoinTable(Table table) {
        super.afterJoinTable(table);
        this.tableManager.raiseHands(this, table.getTableId());
    }

    @Override
    public void afterGameOver(Table table) {
        super.afterGameOver(table);
        this.tableManager.raiseHands(this,table.getTableId());
    }

    @Override
    public void afterTableFull(Table table) {

    }
}
