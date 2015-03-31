package com.jfreer.game;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.player.TestPlayer;

import java.util.Random;


public class Main {
    public static Random rd = new Random();

    public static void main(String[] args) throws InterruptedException {
        final TableManager manager = new TableManager();
        Player player = new TestPlayer(Ids.playerIdGen.getAndIncrement(), manager);
        manager.joinTable(player, null);
        //executorService.shutdown();
        //executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
