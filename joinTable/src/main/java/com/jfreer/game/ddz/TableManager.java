package com.jfreer.game.ddz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by landy on 2015/3/7.
 */
public class TableManager implements Runnable {
    private BlockingQueue<TableOperate> joinOrLeftTableQueue = new LinkedBlockingQueue<TableOperate>();
    private Map<Integer, Table> notFullTables = new HashMap<Integer, Table>();
    private Map<Integer, Table> allTables = new HashMap<Integer, Table>();

    public void requestJoinTable(Player player) {
        JoinTable e = new JoinTable();
        e.setPlayer(player);
        joinOrLeftTableQueue.add(e);
    }

    @Override
    public void run() {
        while (true) {
            try {
                TableOperate operate = joinOrLeftTableQueue.take();
                if (operate instanceof JoinTable) {
                    JoinTable join=(JoinTable)operate;
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
                }else if(operate instanceof LeftTable ) {
                    LeftTable leftTable = (LeftTable) operate;
                    Integer destTableId = leftTable.getDestTableId();
                    if (notFullTables.containsKey(destTableId)) {

                    }else {
                        if (allTables.containsKey(destTableId)) {
                            Log.warn("要离开的桌子已经开始游戏，暂时无法退出");
                        }else{
                            Log.warn("要离开的桌子不存在！");
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Table createTable() {
        return null;
    }
}
