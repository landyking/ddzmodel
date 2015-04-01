package com.jfreer.game.ddz.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * User: landy
 * Date: 15/4/1
 * Time: 下午1:21
 */
public class DDZExecutor {
    public static final int TABLE_PROCESS_COUNT = 100;
    private static DDZThreadPoolExecutor LONG_WORD_POOL = new DDZThreadPoolExecutor(TABLE_PROCESS_COUNT + 1);
    private static DDZThreadPoolExecutor SHORT_WORD_POOL = new DDZThreadPoolExecutor(10);

    public static ScheduledThreadPoolExecutor longWorker() {
        return LONG_WORD_POOL;
    }

    public static ScheduledThreadPoolExecutor shortWorker() {
        return SHORT_WORD_POOL;
    }
}
