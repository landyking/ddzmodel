package com.jfreer.game.ddz;

import java.util.concurrent.*;

/**
 * User: landy
 * Date: 15/3/8
 * Time: 下午3:29
 */
public class DDZThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    public static final DDZThreadPoolExecutor INSTANCE = new DDZThreadPoolExecutor(100);

    private DDZThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (t != null) {
            t.printStackTrace();
        } else {
            if (r instanceof Future) {
                try {
                    Future f = (Future) r;
                    if (f.isDone() && !f.isCancelled()) {
                        f.get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
