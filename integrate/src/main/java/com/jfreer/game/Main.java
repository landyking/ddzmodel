package com.jfreer.game;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.TableManager;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static Random rd = new Random();

    public static void main(String[] args) throws InterruptedException {
        final TableManager manager = new TableManager();
        ExecutorService executorService = Executors.newCachedThreadPool();

        Player player = new Player(Ids.playerIdGen.getAndIncrement());
        manager.joinTable(player, null,null);
        //executorService.shutdown();
        //executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
