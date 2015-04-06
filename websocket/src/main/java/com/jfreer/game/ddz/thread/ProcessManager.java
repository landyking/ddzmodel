package com.jfreer.game.ddz.thread;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: landy
 * Date: 15/4/1
 * Time: 下午3:28
 */
public class ProcessManager {

    private TableOperateProcess[] processQueue;
    private AtomicInteger childIndex = new AtomicInteger();

    public ProcessManager(int size) {
        processQueue=new TableOperateProcess[size];
        for (int i = 0; i < size; i++) {
            TableOperateProcess process = new TableOperateProcess(i);
            DDZExecutor.longWorker().execute(process);
            processQueue[i]=process;
        }
    }

    public TableOperateProcess registerProcessListener(int tableId, TableOperateListener listener) {
        TableOperateProcess poll = processQueue[Math.abs(childIndex.getAndIncrement()%processQueue.length)];
        poll.addListener(tableId, listener);
        return poll;
    }

    public void unregisterProcessListener(int tableId, TableOperateProcess process) {
        process.removeListener(tableId);
    }
}
