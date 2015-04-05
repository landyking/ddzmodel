package com.jfreer.game;

import com.jfreer.game.ddz.Ids;
import com.jfreer.game.ddz.Player;
import com.jfreer.game.ddz.core.TableManager;
import com.jfreer.game.ddz.core.share.TableManagerForShareQueueTable;
import com.jfreer.game.ddz.core.single.TableManagerForSingleQueueTable;
import com.jfreer.game.ddz.player.TestPlayer;
import com.jfreer.game.ddz.thread.ProcessManager;

import java.util.Random;


public class Main {
    public static Random rd = new Random();

    public static void main(String[] args) throws InterruptedException {
        ProcessManager processManager = new ProcessManager(3);
        final TableManager manager = new TableManagerForShareQueueTable(processManager);
//        final TableManager manager = new TableManagerForSingleQueueTable();
        for (int i = 0; i < 20000; i++) {
            Player player = new TestPlayer(Ids.playerIdGen.getAndIncrement(), manager);
            manager.joinTable(player, null);
        }
        //executorService.shutdown();
        //executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
