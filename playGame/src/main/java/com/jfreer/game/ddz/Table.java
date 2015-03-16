package com.jfreer.game.ddz;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public void playCards(Player player, byte[] cards) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setCards(cards);
        boolean operateSuccess = operateQueue.add(e);
    }

    public void playing() {
        //玩牌期间接受的协议
        //出牌,不出,离开托管


        //出牌

        //是否该此玩家出牌
        //1.当前操作位置是否是该玩家的位置

        //玩家是否有此牌
        //1.检查玩家手牌中是否包含要出的牌

        //此牌是否可以出
        //1.检查牌型是否合法
        //2.如果是跟出,则检查是否比被跟的牌大.

        //如果可以出
        //1.取消超时操作
        //2.从手牌中移除此牌
        //3.设置上次出牌玩家为当前用户
        //4.通知所有玩家出牌信息
        //5.操作位置切换为下一个


        //不出

        //是否轮到该玩家
        //1.当前操作位置是否是该玩家的位置

        //是否可以不出
        //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出

        //如果不出
        //1.取消超时操作
        //2.通知所有玩家,不出
        //3.操作位置切换为下一个


        //超时处理
        //1.有牌可出,就出最小的.
        //2.无牌可出,就不出


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
                                    playedCards.addLast(new PlayedCards(currentPos, playCards.getCards()));
                                    //4.通知所有玩家出牌信息
                                    //TODO notifyAllPlayer();
                                    //5.操作位置切换为下一个
                                    currentPos = getNextPos(currentPos);
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

    private boolean isGreaterThanLast(byte[] cards) {
        return CardChecker.isGreater(cards,playedCards.getLast().getCards());
    }

    private void cancelPlayFuture() {
        //TODO
    }

    private int getNextPos(int nextPos) {
        return (nextPos + 1) % players.length;
    }

    private boolean isMaster(Player player) {
        return player.equals(players[playedCards.getLast().getPosition()]);
    }
}
