package com.jfreer.game.ddz;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: landy
 * Date: 15/3/9
 * Time: 下午6:05
 */
public class CallDealerFuture {
    private Future<Void> future;
    private int playerId;

    public CallDealerFuture(Future<Void> future, int playerId) {
        this.future = future;
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    public Void get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }
}
