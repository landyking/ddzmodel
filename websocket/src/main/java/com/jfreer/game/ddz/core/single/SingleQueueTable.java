package com.jfreer.game.ddz.core.single;

import com.jfreer.game.ddz.*;
import com.jfreer.game.ddz.core.Table;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.exception.CardNotExistException;
import com.jfreer.game.ddz.exception.DDZException;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.ddz.exception.TableAlreadyFullException;
import com.jfreer.game.ddz.operate.*;
import com.jfreer.game.ddz.player.RobotPlayer;
import com.jfreer.game.ddz.thread.DDZExecutor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by landy on 2015/3/7.
 */
public class SingleQueueTable extends Table {
    public static final int PLAY_TIME_OUT = 30;
    private Integer tableId;
    private Player[] players = new Player[3];
    private boolean[] raise = new boolean[3];
    private TableManager tableManager;
    private int dealerPos = -1;
    private int currentPos = -1;
    private byte[] belowCards;
    private Consts.TableState tableState;
    private ScheduledFuture<?> playFuture;
    private Future<?> tableFuture;
    private byte[] callDealerFlag = new byte[3];
    private byte orderNo = 1;
    private Random rd = new Random();
    private BlockingQueue<IOperate> operateQueue = new LinkedBlockingQueue<IOperate>();
    private LinkedList<HistoryCards> historyCards = new LinkedList<HistoryCards>();

    public SingleQueueTable(Integer tableId, TableManagerForSingleQueueTable tableManager) {
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

    void joinTable(Player player) throws TableAlreadyFullException {
        stopPlayFuture();
        boolean addSuccess = false;
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                addSuccess = true;
                break;
            }
        }
        if (!addSuccess) {
            throw new TableAlreadyFullException(this.getTableId());
        }

        player.setCurrentTable(this);

        player.afterJoinTable(this);
        if (!isFull()) {
            playFuture = DDZExecutor.shortWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    RobotPlayer robot = new RobotPlayer(Ids.playerIdGen.getAndIncrement(), tableManager);
                    tableManager.joinTable(robot, tableId);
                }
            }, (3 - playerCount()) * 10, TimeUnit.SECONDS);
        } else {
            for (Player one : players) {
                one.afterTableFull(this);
            }
        }

    }

    void raiseHands(Player player) throws PlayerNotOnTheTableException {
        int pos = getPlayerPos(player);
        this.raise[pos] = true;
        Log.info(getTableId(),player.toString() + " raise hands!");
        if (isAllRaise()) {
            restartGame();
        }
    }

    private boolean isAllRaise() {
        for (boolean one : this.raise) {
            if (!one) {
                return false;
            }
        }
        return true;
    }

    public int getPlayerPos(Player player) throws PlayerNotOnTheTableException {
        for (int i = 0; i < players.length; i++) {
            if (player.equals(players[i])) {
                return i;
            }
        }
        throw new PlayerNotOnTheTableException(player.toString(), this.getTableId());
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

        tableFuture = DDZExecutor.longWorker().submit(new Runnable() {
            @Override
            public void run() {
                boolean isException = false;
                String oldName = Thread.currentThread().getName();
                Thread.currentThread().setName("table-main-" + getTableId());
                try {
                    startGame();
                    onGameOver();
                } catch (InterruptedException e) {
                    //TODO
                    e.printStackTrace();
                    isException = true;
                } catch (DDZException e) {
                    //TODO
                    e.printStackTrace();
                    isException = true;
                } catch (Exception e) {
                    //TODO
                    e.printStackTrace();
                    isException = true;
                }
                if (isException) {
                    cleanTableAfterException();
                }
                Thread.currentThread().setName(oldName);
                Log.info(getTableId(),"@@@@@@@@@@@@@@@@@@@@@@@@");
            }
        });
    }

    /**
     * TODO
     * 如果执行过程出现未知异常
     * 记录下异常信息,将该桌所有玩家离桌,然后强制下线(暂定方案)
     */
    private void cleanTableAfterException() {

    }

    private void startGame() throws InterruptedException, DDZException {
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
        Log.info(getTableId(),"当前地主为:" + players[dealerPos]);
        publishBlowCards();
        startPlaying();
    }


    private void publishBlowCards() {
        Log.info(getTableId(),"给地主发底牌...");
        players[dealerPos].addHandCards(this.belowCards);
        for (Player one : players) {
            Log.info(getTableId(),one.toString() + ":" + one.getHandCards());
        }
    }

    private boolean doCallDealer() throws InterruptedException {
        players[currentPos].notifyCallDealer(this, orderNo);
        playFuture = DDZExecutor.shortWorker().schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
        while (true) {
            IOperate operate = operateQueue.take();
            if (operate instanceof CallDealer) {
                CallDealer callDealer = (CallDealer) operate;
                if (callDealer.getOrderNo() != this.orderNo) {
                    Log.info(getTableId(),"orderNo is error!" + callDealer.getOrderNo());
                    continue;
                } else {
                    this.orderNo++;
                }
                Player player = players[currentPos];
                if (callDealer.getPlayer().equals(player)) {
                    boolean doWork = true;
                    if (tableState == Consts.TableState.CallDealer) {
                        stopPlayFuture();
                        if (callDealer.isCall()) {
                            Log.info(getTableId(),player + "叫地主!" + callDealer.getOrderNo());
                            dealerPos = currentPos;
                            tableState = Consts.TableState.RaiseDealer;
                            //Log.info("进入抢地主状态....");
                        } else {
                            Log.info(getTableId(),player + "不叫!" + callDealer.getOrderNo());
                            callDealerFlag[currentPos] = 0;
                            if (noPlayerCall()) {
                                return false;
                            }
                        }
                    } else if (tableState == Consts.TableState.RaiseDealer) {
                        stopPlayFuture();
                        if (callDealer.isCall()) {
                            Log.info(getTableId(),player + "抢地主!" + callDealer.getOrderNo());
                            dealerPos = currentPos;
                            callDealerFlag[currentPos] = 0;
                        } else {
                            Log.info(getTableId(),player + "不抢!" + callDealer.getOrderNo());
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
                            players[currentPos].notifyCallDealer(this, orderNo);
                            playFuture = DDZExecutor.shortWorker().schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
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

    private void stopPlayFuture() {
        if (playFuture != null && !playFuture.isDone()) {
            playFuture.cancel(true);
        }
    }

    private void initCallDealer() {
        currentPos = rd.nextInt(3);
        Log.info(getTableId(),"随机指定第一个叫地主的玩家:" + players[currentPos]);
        tableState = Consts.TableState.CallDealer;
    }

    private void publishCards() {
        Log.info(getTableId(),"发牌....");
        tableState = Consts.TableState.PublishCard;
        Log.info(getTableId(),"发玩家手牌....");
        byte[][] cards = CardUtils.getTableCards(this);
        for (int i = 0; i < players.length; i++) {
            players[i].addHandCards(cards[i]);
            Log.info(getTableId(),players[i].toString() + ":" + players[i].getHandCards());
        }
        Log.info(getTableId(),"获取底牌,隐藏...");
        belowCards = cards[players.length];
        Log.info(getTableId(),"below cards:" + Arrays.toString(belowCards));
    }

    public boolean playCards(Player player, byte[] cards, byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setCards(cards);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateQueue.add(e);
    }

    public boolean playNothing(Player player, byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateQueue.add(e);
    }

    private void initTableAndPlayer() {
        Log.info(getTableId(),"#########################");
        Log.info(getTableId(),"重置桌子参数...");
        stopPlayFuture();
        this.tableState = Consts.TableState.Init;
        this.orderNo = 1;
        Log.info(getTableId(),"重置桌上玩家参数...");
        Arrays.fill(this.callDealerFlag, (byte) 1);
        for (Player one : players) {
            one.getHandCards().clear();
        }
    }

    public void startPlaying() throws InterruptedException, DDZException {

        currentPos = dealerPos;
        Log.info(getTableId(),"牌局开始,地主" + players[currentPos] + "先出");
        players[currentPos].turnToPlay(this, orderNo, null);//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZExecutor.shortWorker().schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);

        while (tableState != Consts.TableState.GameOver) {
            IOperate operate = operateQueue.take();
            if (operate instanceof PlayCards) {
                PlayCards playCards = (PlayCards) operate;
                //是否该此玩家出牌
                //1.当前操作位置是否是该玩家的位置
                Player player = players[currentPos];
                if (player.equals(playCards.getPlayer())) {
                    if (playCards.hasCards()) {
                        //出牌
                        processPlayCards(playCards, player);
                    } else {
                        //不出
                        processPlayNothing(playCards, player);
                    }
                }
            }
        }
        Log.info(getTableId(),"牌局结束!");
    }

    /**
     * 处理出牌
     *
     * @param operate
     * @param player
     */
    private boolean processPlayCards(PlayCards operate, Player player) throws CardNotExistException {
        //玩家是否有此牌
        //1.检查玩家手牌中是否包含要出的牌
        if (!player.hasCards(operate.getCards())) {
            operate.fail("要出的牌不存在!");
            return false;
        }
        //此牌是否可以出
        //1.检查牌型是否合法
        if (!CardUtils.isIegal(operate.getCards())) {
            operate.fail("牌型不合法!");
            return false;
        }
        //2.如果是跟出,则检查是否比被跟的牌大.
        if (isMaster(player) || isGreaterThanLast(operate.getCards())) {
            Log.info(getTableId(),player + "出牌:" + Arrays.toString(operate.getCards()));
            //如果可以出
            //1.取消超时操作
            stopPlayFuture();
            //2.从手牌中移除此牌
            player.removeCards(operate.getCards());
            Log.info(getTableId(),player + ":" + player.getHandCards());
            //3.设置上次出牌玩家为当前用户
            HistoryCards history = new HistoryCards(player.getPlayerId(), operate.getCards());
            historyCards.addLast(history);
            //4.通知所有玩家出牌信息
            notifyAllPlayer(history);
            //5.检查该玩家的牌是否出完,出完则gameover
            if (player.getHandCards().isEmpty()) {
                Log.info(getTableId(),player + "牌出完!");
                this.tableState = Consts.TableState.GameOver;
                return true;
            }
            //6.操作位置切换为下一个
            currentPos = getNextPos(currentPos);
            orderNo++;
            players[currentPos].turnToPlay(this, orderNo, historyCards.getLast());//通知下个玩家出牌
            //设置超时处理器
            playFuture = DDZExecutor.shortWorker().schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
            return true;
        } else {
            operate.fail("没有上家牌大!");
            return false;
        }
    }

    private void onGameOver() {
        //取消所有的举手
        Arrays.fill(this.raise, false);
        //通知每个玩家游戏结束
        //TODO 计算游戏结果,将结果通知每个玩家
        for (Player one : players) {
            one.afterGameOver(this);
        }
        this.operateQueue.clear();
        this.historyCards.clear();
    }

    private boolean processPlayNothing(PlayCards playNothing, Player player) {
        //是否可以不出
        //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出
        if (isMaster(player)) {
            playNothing.fail("必须出牌!");
            return false;
        }
        Log.info(getTableId(),player + "不出牌");
        //如果不出
        //1.取消超时操作
        //2.通知所有玩家,不出
        //3.操作位置切换为下一个
        stopPlayFuture();
        notifyAllPlayer(new HistoryCards(player.getPlayerId(), null));

        currentPos = getNextPos(currentPos);
        orderNo++;
        players[currentPos].turnToPlay(this, orderNo, historyCards.getLast());//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZExecutor.shortWorker().schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
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

    private boolean isMaster(Player player) {
        return historyCards.isEmpty() || player.getPlayerId().equals(historyCards.getLast().getPlayerId());
    }

    public Integer getTableId() {
        return tableId;
    }

    private void removePlayer(Player player) throws PlayerNotOnTheTableException {
        for (int i = 0; i < players.length; i++) {
            if (player.equals(players[i])) {
                players[i] = null;
                return;
            }
        }
        throw new PlayerNotOnTheTableException(player.toString(), this.getTableId());
    }

    private boolean containsPlayer(Player player) {
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

    /**
     * 销毁桌子,对其包含的信息进行清理
     * 当桌子上无人,或者只有机器人时会执行此操作
     */
    public void destory() {
        stopPlayFuture();
        stopTableFuture();
        tableManager = null;
        operateQueue.clear();
        historyCards.clear();
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                try {
                    this.leftTable(players[i]);
                } catch (PlayerNotOnTheTableException e) {
                    e.printStackTrace();
                }
                players[i] = null;
            }
        }

    }

    private void stopTableFuture() {
        if (tableFuture != null && !tableFuture.isDone()) {
            tableFuture.cancel(true);
        }
    }

    public boolean callDealer(Player player, boolean call, byte orderNo) {
        CallDealer e = new CallDealer();
        e.setPlayer(player);
        e.setCall(call);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateQueue.add(e);
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

    public void leftTable(Player player) throws PlayerNotOnTheTableException {
        int playerPos = getPlayerPos(player);
        this.raise[playerPos] = false;
        this.removePlayer(player);
        player.afterLeftTable(this);
        Log.info(getTableId(),String.format("%s left table %s !", player.toString(), getTableId()));
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
            SingleQueueTable.this.callDealer(player, call, this.orderNo);
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
            Log.info(getTableId(),player + "超时,自动出牌!");
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
