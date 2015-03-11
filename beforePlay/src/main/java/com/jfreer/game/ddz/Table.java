package com.jfreer.game.ddz;

import java.util.Random;
import java.util.concurrent.*;

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
    private int currentPos = -1;
    private byte[] blowCards;
    private Consts.TableState tableState;
    private ScheduledFuture<?> future;
    private Future<?> tableFuture;

    private void publishCards() {
        System.out.println("发牌....");
        tableState = Consts.TableState.PublishCard;
        System.out.println("发玩家手牌....");
        byte[][] cards = CardManager.getTableCards();
        for (int i = 0; i < players.length; i++) {
            players[i].setHandCards(cards[i]);
        }
        System.out.println("获取底牌,隐藏...");
        blowCards = cards[players.length];

    }

    public Table() {
    }

    private void mainLoop() {
        while (true) {
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
                System.out.println("当前地主为:" + players[currentPos]);
                publishBlowCards();
                playingCards();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void playingCards() {
        System.out.println("游戏开始..");
        System.out.println("地主先出牌...");
    }

    private void publishBlowCards() {
        System.out.println("展示底牌...");
    }

    private boolean doCallDealer() throws InterruptedException {
        DDZThreadPoolExecutor.INSTANCE.execute(new NotifyCallDealer(players[currentPos]));
        future = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoCallDealer(players[currentPos], false), 30, TimeUnit.SECONDS);
        while (true) {
            TableOperate operate = operateQueue.take();
            if (operate instanceof CallDealer) {
                CallDealer callDealer = (CallDealer) operate;
                Player player = players[currentPos];
                if (callDealer.getPlayer().equals(player)) {
                    if (tableState == Consts.TableState.CallDealer) {
                        stopFuture();
                        if (callDealer.isCall()) {
                            System.out.println(player + "叫地主!");
                            player.setCallDealerState(Consts.CallDealerState.call);
                            dealerId = currentPos;

                            Player nextPlayer = players[getNextPos(this.currentPos)];
                            if (Consts.CallDealerState.notCall == nextPlayer.getCallDealerState()) {
                                //最后一个叫地主
                                tableState = Consts.TableState.Playing;
                                return true;
                            }else{
                                tableState = Consts.TableState.RaiseDealer;
                                System.out.println("进入抢地主状态....");
                            }
                        } else {
                            System.out.println(player + "不叫!");
                            player.setCallDealerState(Consts.CallDealerState.notCall);

                            Player nextPlayer = players[getNextPos(this.currentPos)];
                            if (Consts.CallDealerState.notCall == nextPlayer.getCallDealerState()) {
                                return false;
                            }
                        }
                        currentPos = getNextPos(currentPos);
                        DDZThreadPoolExecutor.INSTANCE.execute(new NotifyCallDealer(players[currentPos]));
                        future = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoCallDealer(players[currentPos], false), 30, TimeUnit.SECONDS);
                    } else if (tableState == Consts.TableState.RaiseDealer) {
                        stopFuture();
                        if (callDealer.isCall()) {
                            System.out.println(player + "抢地主!");
                            dealerId = currentPos;
                            if (player.getCallDealerState() == Consts.CallDealerState.call) {
                                tableState = Consts.TableState.Playing;
                                return true;
                            }
                            player.setCallDealerState(Consts.CallDealerState.raise);
                        } else {
                            System.out.println(player + "不抢!");
                            if (Consts.CallDealerState.call == player.getCallDealerState()) {
                                tableState = Consts.TableState.Playing;
                                return true;
                            } else {
                                player.setCallDealerState(Consts.CallDealerState.notRaise);
                            }
                        }
                        currentPos = getNextPos(currentPos);
                        Consts.CallDealerState nextState = players[currentPos].getCallDealerState();
                        if (nextState == Consts.CallDealerState.notCall || nextState == Consts.CallDealerState.notRaise) {
                            CallDealer e = new CallDealer();
                            e.setCall(false);
                            e.setPlayer(players[currentPos]);
                            operateQueue.add(e);
                        } else {
                            DDZThreadPoolExecutor.INSTANCE.execute(new NotifyCallDealer(players[currentPos]));
                            future = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoCallDealer(players[currentPos], false), 30, TimeUnit.SECONDS);
                        }

                    }
                }
            }

        }
//        return false;
    }

    private void stopFuture() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    private void initCallDealer() {
        currentPos = rd.nextInt(3);
        System.out.println("随机指定第一个叫地主的玩家:" + currentPos);
        tableState = Consts.TableState.CallDealer;
    }

    private void initTableAndPlayer() {
        System.out.println("#########################");
        System.out.println("##                     ##");
        System.out.println("                         ");
        System.out.println("重置桌子参数...");
        tableState = Consts.TableState.Init;
        System.out.println("重置桌上玩家参数...");
        for (Player one : players) {
            one.setCallDealerState(Consts.CallDealerState.def);
        }
    }

    public void callDealer(Player player, boolean call) {
        CallDealer e = new CallDealer();
        e.setPlayer(player);
        e.setCall(call);
        boolean success = operateQueue.add(e);
    }

    private int getNextPos(int nextPos) {
        return (nextPos + 1) % players.length;
    }

    public void joinPlayer(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
        throw new RuntimeException("table is full!!!");
    }

    public void restartGame() {
        if (tableFuture != null && !tableFuture.isDone()) {
            tableFuture.cancel(true);
        }
        tableFuture = DDZThreadPoolExecutor.INSTANCE.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mainLoop();
                return null;
            }
        });
    }

    private class AutoCallDealer implements Runnable {

        private final Player player;
        private final boolean call;

        public AutoCallDealer(Player player, boolean call) {
            this.player = player;
            this.call = call;
        }

        @Override
        public void run() {
            Table.this.callDealer(player, call);
        }
    }

    private class NotifyCallDealer implements Runnable {
        private final Player player;

        public NotifyCallDealer(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.notifyCallDealer(Table.this);
        }
    }
}
