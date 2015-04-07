package com.jfreer.game.ddz.core;

import com.jfreer.game.ddz.*;
import com.jfreer.game.ddz.exception.CardNotExistException;
import com.jfreer.game.ddz.exception.DDZException;
import com.jfreer.game.ddz.exception.PlayerNotOnTheTableException;
import com.jfreer.game.ddz.exception.TableAlreadyFullException;
import com.jfreer.game.ddz.operate.*;
import com.jfreer.game.ddz.player.RobotPlayer;
import com.jfreer.game.ddz.thread.DDZExecutor;
import com.jfreer.game.ddz.thread.ProcessManager;
import com.jfreer.game.ddz.thread.TableOperateListener;
import com.jfreer.game.ddz.thread.TableOperateProcess;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/4/2.
 */
public class Table implements TableOperateListener {
    public static final int PLAY_TIME_OUT = 30;
    private final ProcessManager processManager;
    private Integer tableId;
    private Player[] players = new Player[3];
    private boolean[] raise = new boolean[3];
    private TableManager tableManager;
    private int dealerPos = -1;
    private int currentPos = -1;
    private byte[] belowCards;
    private Consts.TableState tableState;
    private ScheduledFuture<?> playFuture;
    private byte[] callDealerFlag = new byte[3];
    private byte orderNo = 1;
    private Random rd = new Random();
    private LinkedList<HistoryCards> historyCards = new LinkedList<HistoryCards>();
    private TableOperateProcess operateProcess;

    public Table(Integer tableId, TableManager tableManager, ProcessManager processManager) {
        this.processManager = processManager;
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

    private Player[] getPlayers() {
        return players;
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
                    RobotPlayer robot = new RobotPlayer(Ids.newPlayerId(), tableManager);
                    tableManager.joinTable(robot, tableId);
                }
            },  /**(3 - playerCount()) * 10 **/5, TimeUnit.SECONDS);
        } else {
            for (Player one : players) {
                one.afterTableFull(this);
            }
        }

    }

    void raiseHands(Player player) throws PlayerNotOnTheTableException {
        int pos = getPlayerPos(player);
        this.raise[pos] = true;
        Log.info(getTableId(), player.toString() + " raise hands!");
        if (isAllRaise()) {
            Log.info(getTableId(), "all raise ,start game...");
            startGame();
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

    /**
     * TODO
     * 如果执行过程出现未知异常
     * 记录下异常信息,将该桌所有玩家离桌,然后强制下线(暂定方案)
     */
    private void cleanTableAfterException() {

    }

    private boolean startGame() {
        this.tableState = Consts.TableState.PublishCard;
        operateProcess = processManager.registerProcessListener(getTableId(), this);
        PublishCards operate = new PublishCards();
        operate.setDestTableId(getTableId());
        return operateProcess.addOperate(operate);
    }


    private void publishBlowCards() {
        Log.info(getTableId(), "给地主发底牌...");
        players[dealerPos].publishBelowCards(this.belowCards);
        for (Player one : players) {
            Log.info(getTableId(), one.toString() + ":" + one.getHandCards());
        }
    }

    private void doCallDealer(CallDealer callDealer) {
        Player player = players[currentPos];
        if (callDealer.getPlayer().equals(player)) {
            stopPlayFuture();
            if (callDealer.isCall()) {
                Log.info(getTableId(), player + "叫地主!" + callDealer.getOrderNo());
                dealerPos = currentPos;
                tableState = Consts.TableState.RaiseDealer;
                //Log.info(getTableId(),"进入抢地主状态....");
            } else {
                Log.info(getTableId(), player + "不叫!" + callDealer.getOrderNo());
                callDealerFlag[currentPos] = 0;
                if (noPlayerCall()) {
                    notifyCallDealer(currentPos, -1, callDealer.isCall());
                    this.tableState = Consts.TableState.PublishCard;
                    PublishCards event = new PublishCards();
                    event.setDestTableId(getTableId());
                    operateProcess.addOperate(event);
                    return;
                }
            }
            checkOverAndNotifyNextCallDealer(callDealer);
        }
    }


    private void doRaiseDealer(CallDealer callDealer) {
        Player player = players[currentPos];
        if (callDealer.getPlayer().equals(player)) {
            stopPlayFuture();
            if (callDealer.isCall()) {
                Log.info(getTableId(), player + "抢地主!" + callDealer.getOrderNo());
                dealerPos = currentPos;
                callDealerFlag[currentPos] = 0;
            } else {
                Log.info(getTableId(), player + "不抢!" + callDealer.getOrderNo());
                callDealerFlag[currentPos] = 0;
            }
            checkOverAndNotifyNextCallDealer(callDealer);
        }
    }

    /**
     * @param tablePos     当前操作的玩家位置
     * @param nextTablePos 下一个执行操作的玩家的位置,-1表示叫(抢)地主结束,没有下一个
     * @param call
     */
    private void notifyCallDealer(final int tablePos, final int nextTablePos, final boolean call) {
        eachPlayer(new ApplyPlayer() {
            @Override
            public void apply(Player one) {
                one.notifyCallDealer(tablePos, nextTablePos, call);
            }
        });
    }

    private void checkOverAndNotifyNextCallDealer(CallDealer callDealer) {
        int oldPos = currentPos;

        if (isDealerSure()) {
            //最后一个叫地主
            notifyCallDealer(oldPos, -1, callDealer.isCall());

            tableState = Consts.TableState.PublishBelowCard;
            PublishBelowCards event = new PublishBelowCards();
            event.setDestTableId(getTableId());
            operateProcess.addOperate(event);
            Log.info(getTableId(), "地主为" + players[dealerPos]);
            return;
        }

        currentPos = getNextPos(currentPos);

        if (callDealerFlag[currentPos] == 0) {
            //针对前一次没有叫地主的玩家，直接跳过，默认为不抢
            currentPos = getNextPos(currentPos);
        }
//        players[currentPos].notifyCallDealer(this, orderNo);
        notifyCallDealer(oldPos, currentPos, callDealer.isCall());
        playFuture = DDZExecutor.shortWorker().schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
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
        Log.info(getTableId(), "随机指定第一个叫地主的玩家:" + players[currentPos]);
        tableState = Consts.TableState.CallDealer;
        //通知玩家叫牌
        //players[currentPos].notifyCallDealer(this, orderNo);
        notifyBeginPlay();
        playFuture = DDZExecutor.shortWorker().schedule(new AutoCallDealer(players[currentPos], false, orderNo), 30, TimeUnit.SECONDS);
    }

    private void notifyBeginPlay() {
        eachPlayer(new ApplyPlayer() {
            @Override
            public void apply(Player one) {
                one.notifyBeginPlay(currentPos);
            }
        });
    }

    private void publishCards() {
        Log.info(getTableId(), "发牌....");
        tableState = Consts.TableState.PublishCard;
        Log.info(getTableId(), "发玩家手牌....");
        byte[][] cards = CardUtils.getTableCards(this);
        for (int i = 0; i < players.length; i++) {
            players[i].publishHandCards(cards[i]);
            Log.info(getTableId(), players[i].toString() + ":" + players[i].getHandCards());
        }
        Log.info(getTableId(), "获取底牌,隐藏...");
        belowCards = cards[players.length];
        Log.info(getTableId(), "below cards:" + Arrays.toString(belowCards));
    }

    public boolean playCards(Player player, byte[] cards, byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setCards(cards);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateProcess.addOperate(e);
    }

    public boolean playNothing(Player player, byte orderNo) {
        PlayCards e = new PlayCards();
        e.setPlayer(player);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateProcess.addOperate(e);
    }

    private void initTableAndPlayer() {
        Log.info(getTableId(), "#########################");
        Log.info(getTableId(), "重置桌子参数...");
        stopPlayFuture();
        this.tableState = Consts.TableState.Init;
        this.orderNo = 1;
        Log.info(getTableId(), "重置桌上玩家参数...");
        Arrays.fill(this.callDealerFlag, (byte) 1);
        for (Player one : players) {
            one.getHandCards().clear();
        }
    }


    private void notifyDealerPlay() {
        currentPos = dealerPos;
        Log.info(getTableId(), "牌局开始,地主" + players[currentPos] + "先出");
        players[currentPos].turnToPlay(this, orderNo, null);//通知下个玩家出牌
        //设置超时处理器
        playFuture = DDZExecutor.shortWorker().schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
    }

    /**
     * 处理出牌
     *
     * @param operate
     * @param player
     */
    private void processPlayCards(PlayCards operate, Player player) throws CardNotExistException {
        //玩家是否有此牌
        //1.检查玩家手牌中是否包含要出的牌
        if (!player.hasCards(operate.getCards())) {
            operate.fail("要出的牌不存在!");
            return;
        }
        //此牌是否可以出
        //1.检查牌型是否合法
        if (!CardUtils.isIegal(operate.getCards())) {
            operate.fail("牌型不合法!");
            return;
        }
        //2.如果是跟出,则检查是否比被跟的牌大.
        if (isMaster(player) || isGreaterThanLast(operate.getCards())) {
            Log.info(getTableId(), player + "出牌:" + Arrays.toString(operate.getCards()));
            //如果可以出
            //1.取消超时操作
            stopPlayFuture();
            //2.从手牌中移除此牌
            player.removeCards(operate.getCards());
            //Log.info(getTableId(),player + ":" + player.getHandCards());
            //3.设置上次出牌玩家为当前用户
            HistoryCards history = new HistoryCards(player.getPlayerId(), operate.getCards());
            historyCards.addLast(history);
            //4.通知所有玩家出牌信息
            notifyAllPlayer(history);
            //5.检查该玩家的牌是否出完,出完则gameover
            if (player.getHandCards().isEmpty()) {
                Log.info(getTableId(), player + "牌出完!");
                this.tableState = Consts.TableState.GameOver;
                GameOverEvent event = new GameOverEvent();
                event.setDestTableId(getTableId());
                operateProcess.addOperate(event);
                return;
            }
            //6.操作位置切换为下一个
            currentPos = getNextPos(currentPos);
            orderNo++;
            players[currentPos].turnToPlay(this, orderNo, historyCards.getLast());//通知下个玩家出牌
            //设置超时处理器
            playFuture = DDZExecutor.shortWorker().schedule(new AutoPlay(players[currentPos], this.orderNo), PLAY_TIME_OUT, TimeUnit.SECONDS);
        } else {
            operate.fail("没有上家牌大!");
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
        if (this.operateProcess != null) {
            this.processManager.unregisterProcessListener(getTableId(), this.operateProcess);
        }
        this.historyCards.clear();
    }

    private boolean processPlayNothing(PlayCards playNothing, Player player) {
        //是否可以不出
        //1.如果上次出牌玩家为当前玩家,则必须出,否则可以不出
        if (isMaster(player)) {
            playNothing.fail("必须出牌!");
            return false;
        }
        Log.info(getTableId(), player + "不出牌");
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
        tableManager = null;
        if (this.operateProcess != null) {
            this.processManager.unregisterProcessListener(getTableId(), this.operateProcess);
        }
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

    public boolean callDealer(Player player, boolean call, byte orderNo) {
        CallDealer e = new CallDealer();
        e.setPlayer(player);
        e.setCall(call);
        e.setOrderNo(orderNo);
        e.setDestTableId(getTableId());
        return operateProcess.addOperate(e);
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
        Log.info(getTableId(), String.format("%s left table %s !", player.toString(), getTableId()));
    }

    public void eachPlayer(ApplyPlayer ap) {
        for (Player one : this.players) {
            ap.apply(one);
        }
    }

    public static interface ApplyPlayer {
        public void apply(Player one);
    }

    @Override
    public void process(TableOperate event) {
        try {
            switch (this.tableState) {
                case Init:
                    break;
                case PublishCard:
                    if (event instanceof PublishCards) {
                        initTableAndPlayer();
                        //发牌
                        publishCards();
                        //重置叫牌标识 //通知第一个人叫牌
                        //TODO 增加延迟3秒 ,增加一个阶段，InitCallDealer
                        initCallDealer();
                    }
                    break;
                case CallDealer:
                    //处理叫牌
                    //叫牌成功，则状态改为Playing，通知第一个人出牌
                    //如果无人叫牌，则状态改为PublishCard，并向操作列表增加发牌操作
                    if (event instanceof CallDealer) {
                        doCallDealer((CallDealer) event);
                    }
                    break;
                case RaiseDealer:
                    if (event instanceof CallDealer) {
                        doRaiseDealer((CallDealer) event);
                    }
                    break;
                case PublishBelowCard:
                    if (event instanceof PublishBelowCards) {
                        publishBlowCards();
                        this.tableState = Consts.TableState.Playing;
                        //通知地主出牌
                        notifyDealerPlay();
                    }
                    break;
                case Playing:
                    if (event instanceof PlayCards) {
                        PlayCards playCards = (PlayCards) event;
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
                    break;
                case GameOver:
                    if (event instanceof GameOverEvent) {
                        onGameOver();
                    }
                    break;
            }
        } catch (DDZException e) {
            //TODO 游戏过程中出现异常，将该桌玩家全部下线，或全部离桌（先关闭该桌）
            e.printStackTrace();
        }
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


    private class AutoPlay implements Runnable {
        private final Player player;
        private final byte oldOrderNo;

        public AutoPlay(Player player, byte orderNo) {
            this.player = player;
            this.oldOrderNo = orderNo;
        }

        @Override
        public void run() {
            Log.info(getTableId(), player + "超时,自动出牌!");
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
