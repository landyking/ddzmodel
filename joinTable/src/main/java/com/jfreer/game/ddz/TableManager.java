package com.jfreer.game.ddz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by landy on 2015/3/7.
 */
public class TableManager {
    private BlockingQueue<TableOperate> joinOrLeftTableQueue = new LinkedBlockingQueue<TableOperate>();
    private Map<Integer, Table> notFullTables = new HashMap<Integer, Table>();
    private Map<Integer, Table> allTables = new HashMap<Integer, Table>();

    public TableManager() {
        DDZThreadPoolExecutor.INSTANCE.execute(new Runnable() {
            @Override
            public void run() {
                mainLoop();
            }
        });
    }

    public void joinTable(Player player, Integer destTableId, Runnable cb) {
        JoinTable e = new JoinTable();
        e.setPlayer(player);
        e.setDestTableId(destTableId);
        e.setCallback(cb);
        joinOrLeftTableQueue.add(e);
    }

    public void leftTable(Player player, Integer destTableId, Runnable cb) {
        LeftTable e = new LeftTable();
        e.setPlayer(player);
        e.setDestTableId(destTableId);
        e.setCallback(cb);
        joinOrLeftTableQueue.add(e);
    }

    private void mainLoop() {
        while (true) {
            try {
                TableOperate operate = joinOrLeftTableQueue.take();
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
                                Log.warn(String.format("player [%s] can't join table [%s],table is full ", player.toString(), destTableId));
                            } else {
                                Log.warn(String.format("player [%s] can't join table [%s],table is not exist! ", player.toString(), destTableId));
                            }
                            if (join.getCallback() != null) {
                                DDZThreadPoolExecutor.INSTANCE.execute(join.getCallback());
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
                        notifyOneTableFull(table);
                    }
                    if (join.getCallback() != null) {
                        DDZThreadPoolExecutor.INSTANCE.execute(join.getCallback());
                    }
                } else if (operate instanceof LeftTable) {
                    LeftTable leftTable = (LeftTable) operate;
                    Integer destTableId = leftTable.getDestTableId();
                    if (notFullTables.containsKey(destTableId)) {
                        Table table = notFullTables.get(destTableId);
                        if (table.containsPlayer(leftTable.getPlayer())) {
                            table.removePlayer(leftTable.getPlayer());
                        }
                        if (table.isEmpty() || table.isAllRobot()) {
                            table.clear();
                            notFullTables.remove(destTableId);
                            allTables.remove(destTableId);
                            Log.warn("桌子" + destTableId + "为空,回收!");
                            notifyOneTableEmpty(table);
                        }
                    } else {
                        if (allTables.containsKey(destTableId)) {
                            Log.warn("要离开的桌子" + destTableId + "已经开始游戏，暂时无法退出");
                        } else {
                            Log.warn("要离开的桌子不存在！");
                        }
                    }
                    if (leftTable.getCallback() != null) {
                        DDZThreadPoolExecutor.INSTANCE.execute(leftTable.getCallback());
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void notifyOneTableEmpty(Table table) {

    }

    protected void notifyOneTableFull(Table table) {

    }

    private Table createTable() {
        return new Table(Ids.tableIdGen.getAndIncrement(), this);
    }

    public Table[] getAllTables() {
        return allTables.values().toArray(new Table[0]);
    }
}
