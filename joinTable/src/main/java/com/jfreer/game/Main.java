package com.jfreer.game;

import com.jfreer.game.ddz.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * User: landy
 * Date: 15/3/8
 * Time: 下午4:15
 */
public class Main {
    public static Random rd = new Random();

    public static void main(String[] args) throws InterruptedException {
        final TableManager manager = new TableManager();
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(rd.nextInt(15));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final Player player = new Player(Ids.playerIdGen.getAndIncrement());
                    manager.joinTable(player, null, new Runnable() {
                        @Override
                        public void run() {
                            if (player.getCurrentTableId() != null) {
                                try {
                                    TimeUnit.SECONDS.sleep(rd.nextInt(20));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                manager.leftTable(player,player.getCurrentTableId(),null);
                            }
                        }
                    });

                }
            }
        });
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

}
