package com.jfreer.game.ddz;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by landy on 2015/3/7.
 */
public class Table {
    private Integer tableId;
    private Player[] players = new Player[3];
    private TableManager tableManager;
    private int dealerId = -1;
    private int currentPos = -1;
    private byte[] blowCards;
    private Consts.TableState tableState;
    private ScheduledFuture<?> future;
    private Future<?> tableFuture;
    private byte[] callDealerFlag = new byte[3];
    private byte orderNo = 1;
    private Random rd = new Random();
    private BlockingQueue<TableOperate> operateQueue = new LinkedBlockingQueue<TableOperate>();

    public Table(Integer tableId, TableManager tableManager) {
        this.tableId = tableId;
        this.tableManager = tableManager;
    }

    public boolean isFull() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                return false;
            }
        }
        return true;
    }

    public void joinTable(Player player) {
        stopFuture();
        boolean addSuccess = false;
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                addSuccess = true;
                break;
            }
        }
        if (!addSuccess) {
            throw new RuntimeException("table is full!!!");
        }

        player.setCurrentTableId(getTableId());
        if (!isFull()) {
            future = DDZThreadPoolExecutor.INSTANCE.schedule(new Runnable() {
                @Override
                public void run() {
                    RobotPlayer robot = new RobotPlayer(Ids.playerIdGen.getAndIncrement());
                    tableManager.joinTable(robot, tableId, null);
                }
            }, (3 - playerCount()) * 10, TimeUnit.SECONDS);
        } else {
            restartGame();
        }
        System.out.println(String.format("%s join table %s !", player.toString(), getTableId()));
    }

    private int playerCount() {
        int count = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                count++;
            }
        }
        return count;
    }

    public void restartGame() {
        stopTableFuture();
        tableFuture = DDZThreadPoolExecutor.INSTANCE.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                startGame();
                return null;
            }
        });
    }

    private void startGame() throws InterruptedException {
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
        System.out.println("当前地主为:" + players[dealerId]);
        publishBlowCards();
        playingCards();
    }

    private void playingCards() {
        System.out.println("游戏开始..");
        System.out.println("地主先出牌...");
    }

    private void publishBlowCards() {
        System.out.println("展示底牌...");
    }

    private boolean doCallDealer() throws InterruptedException {
        DDZThreadPoolExecutor.INSTANCE.execute(new NotifyCallDealer(players[currentPos], orderNo));
        future = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
        while (true) {
            TableOperate operate = operateQueue.take();
            if (operate instanceof CallDealer) {
                CallDealer callDealer = (CallDealer) operate;
                if (callDealer.getOrderNo() != this.orderNo) {
                    System.out.println("orderNo is error!" + callDealer.getOrderNo());
                    continue;
                } else {
                    this.orderNo++;
                }
                Player player = players[currentPos];
                if (callDealer.getPlayer().equals(player)) {
                    boolean doWork = true;
                    if (tableState == Consts.TableState.CallDealer) {
                        stopFuture();
                        if (callDealer.isCall()) {
                            System.out.println(player + "叫地主!" + callDealer.getOrderNo());
                            dealerId = currentPos;
                            tableState = Consts.TableState.RaiseDealer;
                            //System.out.println("进入抢地主状态....");
                        } else {
                            System.out.println(player + "不叫!" + callDealer.getOrderNo());
                            callDealerFlag[currentPos] = 0;
                            if (noPlayerCall()) {
                                return false;
                            }
                        }
                        if (isDealerSure()) {
                            //最后一个叫地主
                            tableState = Consts.TableState.Playing;
                            return true;
                        }
                    } else if (tableState == Consts.TableState.RaiseDealer) {
                        stopFuture();
                        if (callDealer.isCall()) {
                            System.out.println(player + "抢地主!" + callDealer.getOrderNo());
                            dealerId = currentPos;
                            callDealerFlag[currentPos] = 0;
                        } else {
                            System.out.println(player + "不抢!" + callDealer.getOrderNo());
                            callDealerFlag[currentPos] = 0;
                        }
                    } else {
                        doWork = false;
                    }
                    if (doWork) {
                        if (isDealerSure()) {
                            //最后一个叫地主
                            tableState = Consts.TableState.Playing;
                            return true;
                        }

                        currentPos = getNextPos(currentPos);

                        if (callDealerFlag[currentPos] == 0) {
                            CallDealer e = new CallDealer();
                            e.setOrderNo(this.orderNo);
                            e.setCall(false);
                            e.setPlayer(players[currentPos]);
                            operateQueue.add(e);
                        } else {
                            DDZThreadPoolExecutor.INSTANCE.execute(new NotifyCallDealer(players[currentPos], orderNo));
                            future = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
                        }
                    }
                }
            }

        }
//        return false;
    }

    private int getNextPos(int nextPos) {
        return (nextPos + 1) % players.length;
    }

    private void stopFuture() {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    private void initCallDealer() {
        currentPos = rd.nextInt(3);
        System.out.println("随机指定第一个叫地主的玩家:" + players[currentPos]);
        tableState = Consts.TableState.CallDealer;
    }

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

    private void initTableAndPlayer() {
        System.out.println("#########################");
        System.out.println("##                     ##");
        System.out.println("                         ");
        System.out.println("重置桌子参数...");
        this.tableState = Consts.TableState.Init;
        this.orderNo = 1;
        System.out.println("重置桌上玩家参数...");
        Arrays.fill(this.callDealerFlag, (byte) 1);
    }

    public Integer getTableId() {
        return tableId;
    }

    public void removePlayer(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (player.equals(players[i])) {
                players[i] = null;
            }
        }
        System.out.println(String.format("%s left table %s !", player.toString(), getTableId()));
    }

    public boolean containsPlayer(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (player.equals(players[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllRobot() {
        for (Player one : players) {
            if (one instanceof RobotPlayer) {

            } else {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        //TODO
        for (int i = 0; i < players.length; i++) {
            players[i] = null;
        }
        stopFuture();
        stopTableFuture();
    }

    private void stopTableFuture() {
        if (tableFuture != null && !tableFuture.isDone()) {
            tableFuture.cancel(true);
        }
    }

    public void callDealer(Player player, boolean call, byte orderNo) {
        CallDealer e = new CallDealer();
        e.setPlayer(player);
        e.setCall(call);
        e.setOrderNo(orderNo);
        boolean success = operateQueue.add(e);
    }

    /**
     * 特殊情况
     * 所有人都不叫地主
     * 重新发牌，重新叫地主
     *
     * @return
     */
    private boolean noPlayerCall() {
        byte sum = 0;
        for (byte one : callDealerFlag) {
            sum += one;
        }
        return sum == 0;
    }

    /**
     * 是否地主已确定
     *
     * @return
     */
    private boolean isDealerSure() {
        byte sum = 0;
        for (int i = 0; i < callDealerFlag.length; i++) {
            byte one = callDealerFlag[i];
            sum += one;
        }
        if (sum == 0) {
            //所有人都没有叫（抢）地主机会
            return true;
        } else if (dealerId != -1 && sum == 1 && callDealerFlag[dealerId] == 1) {
            //只有一个人有叫（抢）地主的机会，但是这个人已经是地主
            return true;
        }
        return false;
    }

    private class AutoCallDealer implements Runnable {

        private final Player player;
        private final boolean call;
        private final byte orderNo;

        public AutoCallDealer(Player player, boolean call, byte orderNo) {
            this.player = player;
            this.call = call;
            this.orderNo = orderNo;
        }

        @Override
        public void run() {
            Table.this.callDealer(player, call, this.orderNo);
        }
    }

    private class NotifyCallDealer implements Runnable {
        private final Player player;
        private final byte orderNo;

        public NotifyCallDealer(Player player, byte orderNo) {
            this.player = player;
            this.orderNo = orderNo;
        }

        @Override
        public void run() {
            player.notifyCallDealer(Table.this, this.orderNo);
        }
    }
}
