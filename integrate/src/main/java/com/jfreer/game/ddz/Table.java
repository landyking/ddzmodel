package com.jfreer.game.ddz;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by landy on 2015/3/7.
 */
public class Table {
    public static final int PLAY_TIME_OUT = 30;
    private Integer tableId;
    private Player[] players = new Player[3];
    private TableManager tableManager;
    private int dealerPos = -1;
    private int currentPos = -1;
    private byte[] belowCards;
    private Consts.TableState tableState;
    private ScheduledFuture<?> future;
    private Future<?> tableFuture;
    private ScheduledFuture<?> playFuture;
    private byte[] callDealerFlag = new byte[3];
    private byte orderNo = 1;
    private Random rd = new Random();
    private BlockingQueue<TableOperate> operateQueue = new LinkedBlockingQueue<TableOperate>();
    private LinkedList<HistoryCards> historyCards = new LinkedList<HistoryCards>();

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
        System.out.println(String.format("%s join table %s !", player.toString(), getTableId()));
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
        System.out.println("当前地主为:" + players[dealerPos]);
        publishBlowCards();
        startPlaying();
    }


    private void publishBlowCards() {
        System.out.println("给地主发底牌...");
        players[dealerPos].addHandCards(this.belowCards);
        for (Player one : players) {
            System.out.println(one.toString() + ":" + one.getHandCards());
        }
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
                            dealerPos = currentPos;
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
                            dealerPos = currentPos;
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
        byte[][] cards = CardUtils.getTableCards(this);
        for (int i = 0; i < players.length; i++) {
            players[i].addHandCards(cards[i]);
            System.out.println(players[i].toString() + ":" + players[i].getHandCards());
        }
        System.out.println("获取底牌,隐藏...");
        belowCards = cards[players.length];
        System.out.println("below cards:" + Arrays.toString(belowCards));
    }

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

    public void startPlaying() throws InterruptedException {

        currentPos = dealerPos;
        System.out.println("牌局开始,地主" + players[currentPos] + "先出");
        players[currentPos].turnToPlay(this, orderNo, null);//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);

        while (tableState == Consts.TableState.Playing) {
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
        }
        System.out.println("牌局结束!");
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
        if (!CardUtils.isIegal(playCards.getCards())) {
            playCards.fail("牌型不合法!");
            return false;
        }
        //2.如果是跟出,则检查是否比被跟的牌大.
        if (isMaster(player) || isGreaterThanLast(playCards.getCards())) {
            System.out.println(player + "出牌:" + Arrays.toString(playCards.getCards()));
            //如果可以出
            //1.取消超时操作
            cancelPlayFuture();
            //2.从手牌中移除此牌
            player.removeCards(playCards.getCards());
            System.out.println(player + ":" + player.getHandCards());
            //3.设置上次出牌玩家为当前用户
            HistoryCards history = new HistoryCards(player.getPlayerId(), playCards.getCards());
            historyCards.addLast(history);
            //4.通知所有玩家出牌信息
            notifyAllPlayer(history);
            //5.检查该玩家的牌是否出完,出完则gameover
            if (player.getHandCards().isEmpty()) {
                System.out.println(player + "牌出完!");
                this.tableState = Consts.TableState.GameOver;
                clearAllPlayerHandCards();
                return true;
            }
            //6.操作位置切换为下一个
            currentPos = getNextPos(currentPos);
            orderNo++;
            players[currentPos].turnToPlay(this, orderNo, historyCards.getLast());//通知下个玩家出牌
            //设置超时处理器
            playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
            return true;
        } else {
            playCards.fail("没有上家牌大!");
            return false;
        }
    }

    private void clearAllPlayerHandCards() {
        for (Player one : players) {
            one.getHandCards().clear();
        }
    }

    private boolean processPlayNothing(PlayNothing playNothing, Player player) {
        //是否可以不出
        //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出
        if (isMaster(player)) {
            playNothing.fail("必须出牌!");
            return false;
        }
        System.out.println(player + "不出牌");
        //如果不出
        //1.取消超时操作
        //2.通知所有玩家,不出
        //3.操作位置切换为下一个
        cancelPlayFuture();
        notifyAllPlayer(new HistoryCards(player.getPlayerId(), null));

        currentPos = getNextPos(currentPos);
        orderNo++;
        players[currentPos].turnToPlay(this, orderNo, historyCards.getLast());//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
        return true;
    }

    private void notifyAllPlayer(HistoryCards history) {
        for (Player one : players) {
            one.notifyPlayedCards(history);
        }
    }

    private boolean isGreaterThanLast(byte[] cards) {
        return CardUtils.isGreater(cards, historyCards.getLast().getCards());
    }

    private void cancelPlayFuture() {
        if (playFuture != null && !playFuture.isDone()) {
            playFuture.cancel(true);
        }
    }

    private boolean isMaster(Player player) {
        return historyCards.isEmpty() || player.getPlayerId().equals(historyCards.getLast().getPlayerId());
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
            if (players[i] != null) {
                players[i].clear();
                players[i] = null;
            }
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
        } else if (dealerPos != -1 && sum == 1 && callDealerFlag[dealerPos] == 1) {
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

    private class AutoPlay implements Runnable {
        private final Player player;
        private final byte oldOrderNo;

        public AutoPlay(Player player, byte orderNo) {
            this.player = player;
            this.oldOrderNo = orderNo;
        }

        @Override
        public void run() {
            System.out.println(player + "超时,自动出牌!");
            if (isMaster(player)) {
                byte[] tmp = CardUtils.getMinCards(player.getHandCards());
                if (!playCards(player, tmp, oldOrderNo)) {
                    //TODO 超时机制失败
                }
            } else {
                byte[] tmp = CardUtils.getCardsGreaterThan(historyCards.getLast().getCards(), player.getHandCards());
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
