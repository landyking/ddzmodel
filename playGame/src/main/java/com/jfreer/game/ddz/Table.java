package com.jfreer.game.ddz;

import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * User: landy
 * Date: 15/3/13
 * Time: 上午11:38
 */
public class Table {
    private int currentPos;
    private Player[] players = new Player[3];
    private BlockingQueue<TableOperate> operateQueue = new LinkedBlockingQueue<TableOperate>();
    private LinkedList<PlayedCards> playedCards = new LinkedList<PlayedCards>();
    private ScheduledFuture<?> playFuture;
    private byte orderNo = 1;


    public boolean playCards(Player player, byte[] cards,byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setCards(cards);
        e.setOrderNo(orderNo);
        return operateQueue.add(e);
    }

    public boolean playNothing(Player player,byte orderNo) {
        PlayNothing e = new PlayNothing();
        e.setPlayer(player);
        e.setOrderNo(orderNo);
        return operateQueue.add(e);
    }
    public void playing() {
        while (true) {
            try {
                TableOperate operate = operateQueue.take();
                if (operate instanceof PlayCards) {
                    PlayCards playCards = (PlayCards) operate;
                    //出牌

                    //是否该此玩家出牌
                    //1.当前操作位置是否是该玩家的位置
                    Player player = players[currentPos];
                    if (player.equals(playCards.getPlayer())) {
                        //玩家是否有此牌
                        //1.检查玩家手牌中是否包含要出的牌
                        if (player.hasCards(playCards.getCards())) {
                            //此牌是否可以出
                            //1.检查牌型是否合法
                            //2.如果是跟出,则检查是否比被跟的牌大.

                            if (CardChecker.isIegal(playCards.getCards())) {
                                if (isMaster(player) || isGreaterThanLast(playCards.getCards())) {
                                    //如果可以出
                                    //1.取消超时操作
                                    cancelPlayFuture();
                                    //2.从手牌中移除此牌
                                    player.removeCards(playCards.getCards());
                                    //3.设置上次出牌玩家为当前用户
                                    PlayedCards history = new PlayedCards(player.getPlayerId(), playCards.getCards());
                                    playedCards.addLast(history);
                                    //4.通知所有玩家出牌信息
                                    notifyAllPlayer(history);
                                    //5.操作位置切换为下一个
                                    currentPos = getNextPos(currentPos);
                                    orderNo++;
                                    DDZThreadPoolExecutor.INSTANCE.execute(new NotifyPlayCards(players[currentPos],orderNo));
                                    playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new Table.AutoPlay(players[currentPos],this.orderNo), 30, TimeUnit.SECONDS);
                                }
                            }


                        }
                    }


                } else if (operate instanceof PlayNothing) {
                    PlayNothing playNothing = (PlayNothing) operate;
                    //不出

                    //是否轮到该玩家
                    //1.当前操作位置是否是该玩家的位置

                    //是否可以不出
                    //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出

                    //如果不出
                    //1.取消超时操作
                    //2.通知所有玩家,不出
                    //3.操作位置切换为下一个
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAllPlayer(final PlayedCards history) {
        DDZThreadPoolExecutor.INSTANCE.execute(new Runnable() {
            @Override
            public void run() {
                for (Player one : players) {
                    one.notifyPlayedCards(history);
                }
            }
        });
    }

    private boolean isGreaterThanLast(byte[] cards) {
        return CardChecker.isGreater(cards,playedCards.getLast().getCards());
    }

    private void cancelPlayFuture() {
        if (playFuture != null && !playFuture.isDone()) {
            playFuture.cancel(true);
        }
    }

    private int getNextPos(int nextPos) {
        return (nextPos + 1) % players.length;
    }

    private boolean isMaster(Player player) {
        return playedCards.isEmpty() || player.getPlayerId().equals(playedCards.getLast().getPlayerId());
    }

    private class AutoPlay implements Runnable{
        private final Player player;
        private final byte oldOrderNo;

        public AutoPlay(Player player, byte orderNo) {
            this.player=player;
            this.oldOrderNo=orderNo;
        }

        @Override
        public void run() {
            if (isMaster(player)) {
                byte[] tmp=CardChecker.getMinCards(player.getHandCards());
                if (!playCards(player, tmp, oldOrderNo)) {
                    //TODO 超时机制失败
                }
            }else{
                byte[] tmp = CardChecker.getCardsGreaterThan(playedCards.getLast().getCards(),player.getHandCards());
                if (tmp != null && tmp.length > 0) {
                    if (!playCards(player, tmp, oldOrderNo)) {
                        //TODO 超时机制失败
                    }
                } else {
                    if (!playNothing(player, oldOrderNo)) {
                        //TODO 超时机制失败
                    }
                }
            }

        }
    }

    private class NotifyPlayCards implements Runnable {
        private final Player player;
        private final byte oldOrderNo;

        public NotifyPlayCards(Player player, byte orderNo) {
            this.player=player;
            this.oldOrderNo=orderNo;
        }

        @Override
        public void run() {
            player.turnToPlay(oldOrderNo);
        }
    }
}
