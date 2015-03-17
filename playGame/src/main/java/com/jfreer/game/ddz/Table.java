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
    private TableState tableState;


    public boolean playCards(Player player, byte[] cards, byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setCards(cards);
        e.setOrderNo(orderNo);
        return operateQueue.add(e);
    }

    public boolean playNothing(Player player, byte orderNo) {
        PlayNothing e = new PlayNothing();
        e.setPlayer(player);
        e.setOrderNo(orderNo);
        return operateQueue.add(e);
    }

    public void playing() {
        tableState = TableState.playing;
        while (tableState == TableState.playing) {
            try {
                TableOperate operate = operateQueue.take();
                if (operate instanceof PlayCards) {
                    PlayCards playCards = (PlayCards) operate;
                    //出牌

                    //是否该此玩家出牌
                    //1.当前操作位置是否是该玩家的位置
                    Player player = players[currentPos];
                    if (player.equals(playCards.getPlayer())) {
                        processPlayCards(playCards, player);
                    }


                } else if (operate instanceof PlayNothing) {
                    PlayNothing playNothing = (PlayNothing) operate;
                    //不出

                    //是否轮到该玩家
                    //1.当前操作位置是否是该玩家的位置
                    Player player = players[currentPos];
                    if (player.equals(playNothing.getPlayer())) {
                        processPlayNothing(playNothing, player);
                    }


                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean processPlayNothing(PlayNothing playNothing, Player player) {
        //是否可以不出
        //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出
        if (isMaster(player)) {
            playNothing.fail("必须出牌!");
            return false;
        }
        //如果不出
        //1.取消超时操作
        //2.通知所有玩家,不出
        //3.操作位置切换为下一个
        cancelPlayFuture();
        notifyAllPlayer(new PlayedCards(player.getPlayerId(), null));

        currentPos = getNextPos(currentPos);
        orderNo++;
        players[currentPos].turnToPlay(this, orderNo);//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoPlay(players[currentPos], this.orderNo), 30, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 处理出牌
     *
     * @param playCards
     * @param player
     */
    private boolean processPlayCards(PlayCards playCards, Player player) {
        //玩家是否有此牌
        //1.检查玩家手牌中是否包含要出的牌
        if (!player.hasCards(playCards.getCards())) {
            playCards.fail("要出的牌不存在!");
            return false;
        }
        //此牌是否可以出
        //1.检查牌型是否合法
        if (!CardChecker.isIegal(playCards.getCards())) {
            playCards.fail("牌型不合法!");
            return false;
        }
        //2.如果是跟出,则检查是否比被跟的牌大.
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
            //5.检查该玩家的牌是否出完,出完则gameover
            if (player.getHandCards().isEmpty()) {
                this.tableState = TableState.gameover;
                return true;
            }
            //6.操作位置切换为下一个
            currentPos = getNextPos(currentPos);
            orderNo++;
            players[currentPos].turnToPlay(this, orderNo);//通知下个玩家出牌
            //设置超时处理器
            playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoPlay(players[currentPos], this.orderNo), 30, TimeUnit.SECONDS);
            return true;
        } else {
            playCards.fail("没有上家牌大!");
            return false;
        }
    }

    private void notifyAllPlayer(PlayedCards history) {
        for (Player one : players) {
            one.notifyPlayedCards(history);
        }
    }

    private boolean isGreaterThanLast(byte[] cards) {
        return CardChecker.isGreater(cards, playedCards.getLast().getCards());
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

    private class AutoPlay implements Runnable {
        private final Player player;
        private final byte oldOrderNo;

        public AutoPlay(Player player, byte orderNo) {
            this.player = player;
            this.oldOrderNo = orderNo;
        }

        @Override
        public void run() {
            if (isMaster(player)) {
                byte[] tmp = CardChecker.getMinCards(player.getHandCards());
                if (!playCards(player, tmp, oldOrderNo)) {
                    //TODO 超时机制失败
                }
            } else {
                byte[] tmp = CardChecker.getCardsGreaterThan(playedCards.getLast().getCards(), player.getHandCards());
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
}
