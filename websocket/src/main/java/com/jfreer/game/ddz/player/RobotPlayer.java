package com.jfreer.game.ddz.player;

import com.jfreer.game.ddz.CardUtils;
import com.jfreer.game.ddz.HistoryCards;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.ddz.thread.DDZExecutor;
import com.jfreer.game.websocket.protocol.IResp;

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
    public void afterJoinTable(Table table) {
        super.afterJoinTable(table);
        this.tableManager.raiseHands(this, table.getTableId());
    }

    @Override
    public void afterGameOver(Table table) {
        super.afterGameOver(table);
        this.tableManager.raiseHands(this, table.getTableId());
    }

    @Override
    public void pushToClient(IResp resp) {

    }


    @Override
    public void notifyBeginPlay(int nextTablePos) {
        notifyCallDealer(-1,nextTablePos,-1);
    }


    @Override
    public void notifyCallDealer(int tablePos, int nextTablePos, int callFlag) {
        try {
            if (nextTablePos == getCurrentTable().getPlayerPos(this)) {
                DDZExecutor.shortWorker().schedule(new Runnable() {
                    @Override
                    public void run() {
                        getCurrentTable().callDealer(RobotPlayer.this,true /*random.nextBoolean()*/, (byte) 0);
                    }
                }, 3, TimeUnit.SECONDS);
            }
        } catch (PlayerNotOnTheTableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyBelowCards(int dealerPos, byte[] belowCards) {
        notifyPlayedCards(-1,dealerPos,null,null);
    }

    @Override
    public void notifyPlayedCards(int tablePos, int nextTablePos, byte[] cards, final HistoryCards lastHistory) {
        try {
            if(nextTablePos==getCurrentTable().getPlayerPos(this)) {
                /**
                 * 出牌时机:为了更像真人,机器人的出牌延迟在5~15秒之间随机
                 */
                int delay = random.nextInt(10) + 5;

                DDZExecutor.shortWorker().schedule(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 如果是主导者,则选最小的牌出
                         * 如果是跟出者,则选择最小的但是比上家大的牌出,如果没有,则不出.
                         */
                        if (lastHistory == null || getPlayerId().equals(lastHistory.getPlayerId())) {
                            byte[] minCards = CardUtils.getMinCards(getHandCards());
                            getCurrentTable().playCards(RobotPlayer.this, minCards, (byte) 0);
                        } else {
                            byte[] cardsGreaterThan = CardUtils.getCardsGreaterThan(lastHistory.getCards(), getHandCards());
                            if (cardsGreaterThan != null && cardsGreaterThan.length > 0) {
                                getCurrentTable().playCards(RobotPlayer.this, cardsGreaterThan, (byte) 0);
                            } else {
                                getCurrentTable().playNothing(RobotPlayer.this, (byte) 0);
                            }
                        }
                    }
                }, delay, TimeUnit.SECONDS);
            }
        } catch (PlayerNotOnTheTableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void afterTableFull(Table table) {

    }
}
