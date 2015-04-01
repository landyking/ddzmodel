package com.jfreer.game.ddz;

import com.jfreer.game.ddz.thread.DDZExecutor;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DDZThreadPoolExecutorTest {
    @Test
    public void testSize() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 1000; i++) {
            final int finalI = i;
            DDZExecutor.shortWorker().execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            System.out.println("active:"+DDZExecutor.shortWorker().getActiveCount());
        }
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}