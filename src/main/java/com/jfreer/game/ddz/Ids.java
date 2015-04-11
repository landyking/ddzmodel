package com.jfreer.game.ddz;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: landy
 * Date: 15/3/8
 * Time: 下午4:12
 */
public class Ids {
    private static final AtomicInteger tableIdGen = new AtomicInteger(1);
    private static final AtomicInteger playerIdGen = new AtomicInteger(1);

    public static Integer newPlayerId() {
        return playerIdGen.getAndIncrement();
    }
    public static Integer newTableId(){
        return tableIdGen.getAndIncrement();
    }
}
