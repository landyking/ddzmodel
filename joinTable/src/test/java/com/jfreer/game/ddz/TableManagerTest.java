package com.jfreer.game.ddz;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TableManagerTest {
    /**
     * 加入一个人
     * 自动加入两个机器人
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableFull(Table table) {
                latch.countDown();
            }
        };
        manager.joinTable(new Player(Ids.playerIdGen.getAndIncrement()), null, null);
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(1, allTables.length);
        Player[] players = allTables[0].getPlayers();
        int robot = 0;
        for (Player one : players) {
            if (one instanceof RobotPlayer) {
                robot++;
            }
        }
        Assert.assertEquals(2, robot);
    }

    /**
     * 加入2个人
     * 自动加入1个机器人
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableFull(Table table) {
                latch.countDown();
            }
        };
        manager.joinTable(new Player(Ids.playerIdGen.getAndIncrement()), null, null);
        manager.joinTable(new Player(Ids.playerIdGen.getAndIncrement()), null, null);
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(1, allTables.length);
        Player[] players = allTables[0].getPlayers();
        int robot = 0;
        for (Player one : players) {
            if (one instanceof RobotPlayer) {
                robot++;
            }
        }
        Assert.assertEquals(1, robot);
    }

    /**
     * 加入2个人,离开一个
     * 自动加入两个机器人
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableFull(Table table) {
                latch.countDown();
            }
        };
        Player player1 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player1, null, null);
        final Player player2 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player2, null, new Runnable() {
            @Override
            public void run() {
                if (player2.getCurrentTableId() != null) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.leftTable(player2, player2.getCurrentTableId(), null);
                }
            }
        });
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(1, allTables.length);
        Player[] players = allTables[0].getPlayers();
        int robot = 0;
        for (Player one : players) {
            if (one instanceof RobotPlayer) {
                robot++;
            }
        }
        Assert.assertEquals(2, robot);
    }

    @Test
    public void test4() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableFull(Table table) {
                latch.countDown();
            }
        };
        final Player player1 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player1, null, new Runnable() {
            @Override
            public void run() {
                if (player1.getCurrentTableId() != null) {
                    try {
                        TimeUnit.SECONDS.sleep(7);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.leftTable(player1, player1.getCurrentTableId(), null);
                }
            }
        });
        final Player player2 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player2, null, null);
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(1, allTables.length);
        Player[] players = allTables[0].getPlayers();
        int robot = 0;
        for (Player one : players) {
            if (one instanceof RobotPlayer) {
                robot++;
            }
        }
        Assert.assertEquals(2, robot);
    }

    @Test
    public void test5() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableEmpty(Table table) {
                latch.countDown();
            }
        };
        final Player player1 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player1, null, new Runnable() {
            @Override
            public void run() {
                if (player1.getCurrentTableId() != null) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.leftTable(player1, player1.getCurrentTableId(), null);
                }
            }
        });
        final Player player2 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player2, null, new Runnable() {
            @Override
            public void run() {
                if (player2.getCurrentTableId() != null) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.leftTable(player2, player2.getCurrentTableId(), null);
                }
            }
        });
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(0, allTables.length);
    }
    @Test
    public void test6() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final TableManager manager = new TableManager() {
            @Override
            protected void notifyOneTableEmpty(Table table) {
                latch.countDown();
            }
        };
        final Player player1 = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player1, null, new Runnable() {
            @Override
            public void run() {
                if (player1.getCurrentTableId() != null) {
                    try {
                        TimeUnit.SECONDS.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.leftTable(player1, player1.getCurrentTableId(), null);
                }
            }
        });
        latch.await();
        Table[] allTables = manager.getAllTables();
        Assert.assertEquals(0, allTables.length);
    }
}