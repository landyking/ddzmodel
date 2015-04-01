package com.jfreer.game.ddz.core;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Log;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.exception.DDZException;
import com.jfreer.game.ddz.operate.JoinTable;
import com.jfreer.game.ddz.operate.LeftTable;
import com.jfreer.game.ddz.operate.RaiseHands;
import com.jfreer.game.ddz.operate.TableOperate;
import com.jfreer.game.ddz.thread.DDZExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by landy on 2015/3/7.
 */
public class TableManager {
    private BlockingQueue<TableOperate> tableOperateQueue = new LinkedBlockingQueue<TableOperate>();
    private Map<Integer, Table> notFullTables = new HashMap<Integer, Table>();
    private Map<Integer, Table> allTables = new HashMap<Integer, Table>();

    public TableManager() {
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
            TableOperate operate = null;
            try {
                operate = tableOperateQueue.take();
                if (operate instanceof JoinTable) {
                    JoinTable join = (JoinTable) operate;
                    Player player = join.getPlayer();
                    Integer destTableId = join.getDestTableId();
                    Table table;
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
                            Table newTable = createTable();
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
                        Table table = notFullTables.get(destTableId);
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

    protected void notifyOneTableEmpty(Table table) {

    }

    private Table createTable() {
        return new Table(Ids.tableIdGen.getAndIncrement(), this);
    }

    public Table[] getAllTables() {
        return allTables.values().toArray(new Table[0]);
    }
}
