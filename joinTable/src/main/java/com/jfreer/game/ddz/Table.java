package com.jfreer.game.ddz;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by landy on 2015/3/7.
 */
public class Table {
    private Integer tableId;
    private Set<Player> players = new HashSet<Player>();
    private ScheduledFuture<?> addPlayerFuture;
    private TableManager tableManager;

    public Table(Integer tableId, TableManager tableManager) {
        this.tableId = tableId;
        this.tableManager = tableManager;
    }

    public boolean isFull() {
        return players.size() == 3;
    }

    public void joinTable(Player player) {
        if (addPlayerFuture != null && !addPlayerFuture.isDone()) {
            addPlayerFuture.cancel(true);
        }
        players.add(player);
        player.setCurrentTableId(getTableId());
        if (!isFull()) {
            addPlayerFuture = DDZThreadPoolExecutor.INSTANCE.schedule(new Runnable() {
                @Override
                public void run() {
                    RobotPlayer robot = new RobotPlayer(Ids.playerIdGen.getAndIncrement());
                    tableManager.joinTable(robot, tableId, null);
                }
            }, (3 - players.size()) * 10, TimeUnit.SECONDS);
        }
        System.out.println(String.format("%s join table %s !", player.toString(), getTableId()));
    }

    public Integer getTableId() {
        return tableId;
    }

    public void removePlayer(Player player) {
        players.remove(player);
        System.out.println(String.format("%s left table %s !", player.toString(), getTableId()));
    }

    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    public boolean isEmpty() {
        return players.isEmpty();
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
        players.clear();
        if (addPlayerFuture != null && !addPlayerFuture.isDone()) {
            addPlayerFuture.cancel(true);
        }
    }

    public Player[] getPlayers() {
        return
        players.toArray(new Player[0]);
    }
}
