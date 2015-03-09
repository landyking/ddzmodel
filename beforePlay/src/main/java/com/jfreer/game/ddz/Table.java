package com.jfreer.game.ddz;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: landy
 * Date: 15/3/9
 * Time: 上午11:22
 */
public class Table {
    private Random rd = new Random();
    private BlockingQueue<TableOperate> operateQueue = new LinkedBlockingQueue<TableOperate>();
    private Player[] players = new Player[3];
    private int dealerId = -1;
    private int nextPos = -1;
    private byte[] blowCards;
    private Consts.TableState tableState;

    public void publishCards() {
        tableState = Consts.TableState.PublishCard;
        byte[][] cards = CardManager.getTableCards();
        for (int i = 0; i < players.length; i++) {
            players[i].setHandCards(cards[i]);
        }
        blowCards = cards[players.length];

    }

    public Table() {
        DDZThreadPoolExecutor.INSTANCE.execute(new Runnable() {
            @Override
            public void run() {
                processTableOperate();
            }
        });
    }

    public void mainLoop() {
        try {
            /**
             * 1.初始化参数
             * 2.发牌
             * 3.设置初始叫地主的玩家
             * 4.叫地主
             * 5.发底牌
             * 6.地主开始出牌
             * 7.游戏进行
             * 8.游戏结束
             */
            boolean callDealerSuccess = false;
            while (!callDealerSuccess) {
                initTableAndPlayer();
                publishCards();
                initCallDealer();
                callDealerSuccess = doCallDealer();
            }
            publishBlowCards();
            playingCards();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playingCards() {

    }

    private void publishBlowCards() {

    }

    private boolean doCallDealer() throws InterruptedException {
        while (true) {
            TableOperate operate = operateQueue.take();
            if (operate instanceof CallDealer) {
                CallDealer callDealer = (CallDealer) operate;
                if (tableState == Consts.TableState.CallDealer) {
                    Player player = players[nextPos];
                    if (callDealer.getPlayer().equals(player)) {
                        if (callDealer.isCall()) {
                            player.setCallDealerState(Consts.CallDealerState.call);
                            dealerId = player.getPlayerId();

                            tableState = Consts.TableState.RaiseDealer;

                            Player nextPlayer = players[getNextPos(this.nextPos)];
                            if (Consts.CallDealerState.notCall == nextPlayer.getCallDealerState()) {
                                //最后一个叫地主
                                tableState = Consts.TableState.Playing;
                                return true;
                            }
                        } else {
                            player.setCallDealerState(Consts.CallDealerState.notCall);

                            Player nextPlayer = players[getNextPos(this.nextPos)];
                            if (Consts.CallDealerState.notCall == nextPlayer.getCallDealerState()) {
                                return false;
                            }
                        }
                        nextPos = getNextPos(nextPos);
                        DDZThreadPoolExecutor.INSTANCE.execute(new Runnable() {
                            @Override
                            public void run() {
                                players[nextPos].turnCallDealer();
                            }
                        });
                    }
                } else if (tableState == Consts.TableState.RaiseDealer) {
                    Player player = players[nextPos];
                    if (callDealer.getPlayer().equals(player)) {
                        if (callDealer.isCall()) {
                            dealerId = player.getPlayerId();
                            if (player.getCallDealerState() == Consts.CallDealerState.call) {
                                tableState = Consts.TableState.Playing;
                                return true;
                            }
                            player.setCallDealerState(Consts.CallDealerState.raise);
                        } else {
                            player.setCallDealerState(Consts.CallDealerState.notRaise);
                        }
                        nextPos = getNextPos(nextPos);
                    }
                }

            }

        }
//        return false;
    }

    private void initCallDealer() {
        nextPos = rd.nextInt(3);
        tableState = Consts.TableState.CallDealer;
    }

    private void initTableAndPlayer() {

    }

    public void callDealer(Player player, boolean call) {
        CallDealer e = new CallDealer();
        e.setPlayer(player);
        e.setCall(call);
        boolean success = operateQueue.add(e);
    }

    private void processTableOperate() {

    }

    private int getNextPos(int nextPos) {
        return (nextPos + 1) % players.length;
    }

}
