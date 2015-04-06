package com.jfreer.game.ddz.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * User: landy
 * Date: 15/3/8
 * Time: 下午3:29
 */
 class DDZThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    DDZThreadPoolExecutor(int corePoolSize) {
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
