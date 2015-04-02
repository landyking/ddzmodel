package com.jfreer.game.ddz.core.share;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Log;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.exception.DDZException;
import com.jfreer.game.ddz.operate.*;
import com.jfreer.game.ddz.thread.DDZExecutor;
import com.jfreer.game.ddz.thread.ProcessManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by landy on 2015/3/7.
 */
public class TableManagerForShareQueueTable extends TableManager {
    private final ProcessManager processManager;
    private BlockingQueue<TableUserOperate> tableOperateQueue = new LinkedBlockingQueue<TableUserOperate>();
    private Map<Integer, ShareQueueTable> notFullTables = new HashMap<Integer, ShareQueueTable>();
    private Map<Integer, ShareQueueTable> allTables = new HashMap<Integer, ShareQueueTable>();

    public TableManagerForShareQueueTable(ProcessManager processManager) {
        this.processManager=processManager;
        DDZExecutor.longWorker().execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("table-manager");
                mainLoop();
            }
        });
    }

    public void joinTable(Player player, Integer destTableId) {
        JoinTable e = new JoinTable();
        e.setPlayer(player);
        e.setDestTableId(destTableId);
        tableOperateQueue.add(e);
    }

    public void leftTable(Player player, Integer destTableId) {
        LeftTable e = new LeftTable();
        e.setPlayer(player);
        e.setDestTableId(destTableId);
        tableOperateQueue.add(e);
    }

    public void raiseHands(Player player, Integer destTableId) {
        RaiseHands e = new RaiseHands();
        e.setPlayer(player);
        e.setDestTableId(destTableId);
        tableOperateQueue.add(e);
    }

    private void mainLoop() {
        while (true) {
            TableUserOperate operate = null;
            try {
                operate = tableOperateQueue.take();
                if (operate instanceof JoinTable) {
                    JoinTable join = (JoinTable) operate;
                    Player player = join.getPlayer();
                    Integer destTableId = join.getDestTableId();
                    ShareQueueTable table;
                    if (destTableId != null) {
                        if (notFullTables.containsKey(destTableId)) {
                            table = notFullTables.get(destTableId);
                        } else {
                            if (allTables.containsKey(destTableId)) {
                                operate.fail(String.format("player [%s] can't join table [%s],table is full ", player.toString(), destTableId));
                            } else {
                                operate.fail(String.format("player [%s] can't join table [%s],table is not exist! ", player.toString(), destTableId));
                            }
                            continue;
                        }
                    } else {
                        if (notFullTables.isEmpty()) {
                            ShareQueueTable newTable = createTable();
                            allTables.put(newTable.getTableId(), newTable);
                            notFullTables.put(newTable.getTableId(), newTable);
                        }
                        table = notFullTables.values().iterator().next();
                    }
                    table.joinTable(player);
                    if (table.isFull()) {
                        notFullTables.remove(table.getTableId());
                    }
                } else if (operate instanceof LeftTable) {
                    LeftTable leftTable = (LeftTable) operate;
                    Integer destTableId = leftTable.getDestTableId();
                    if (notFullTables.containsKey(destTableId)) {
                        ShareQueueTable table = notFullTables.get(destTableId);
                        table.leftTable(leftTable.getPlayer());
                        if (table.isEmpty() || table.isAllRobot()) {
                            table.destory();
                            notFullTables.remove(destTableId);
                            allTables.remove(destTableId);
                            Log.warn("桌子" + destTableId + "为空,回收!");
                            notifyOneTableEmpty(table);
                        }
                    } else {
                        //TODO 当游戏没有进行时,可以退出
                        if (allTables.containsKey(destTableId)) {
                            operate.fail("要离开的桌子" + destTableId + "已经开始游戏，暂时无法退出");
                        } else {
                            operate.fail("要离开的桌子不存在！" + destTableId);
                        }
                    }
                } else if (operate instanceof RaiseHands) {
                    if (allTables.containsKey(operate.getDestTableId())) {
                        allTables.get(operate.getDestTableId()).raiseHands(operate.getPlayer());
                    } else {
                        operate.fail("桌子不存在！" + operate.getDestTableId());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (DDZException e) {
                if (operate != null) {
                    operate.fail(e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void notifyOneTableEmpty(ShareQueueTable table) {

    }

    private ShareQueueTable createTable() {
        return new ShareQueueTable(Ids.tableIdGen.getAndIncrement(), this,this.processManager);
    }

    public ShareQueueTable[] getAllTables() {
        return allTables.values().toArray(new ShareQueueTable[0]);
    }
}
