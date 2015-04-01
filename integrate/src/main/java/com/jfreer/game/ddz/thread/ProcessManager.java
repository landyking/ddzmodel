package com.jfreer.game.ddz.thread;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * User: landy
 * Date: 15/4/1
 * Time: 下午3:28
 */
public class ProcessManager {

    private static final Comparator<TableOperateProcess> TABLE_OPERATE_PROCESS_COMPARATOR = new Comparator<TableOperateProcess>() {
        @Override
        public int compare(TableOperateProcess o1, TableOperateProcess o2) {
            return o1.listenerCount() - o2.listenerCount();
        }
    };
    private PriorityQueue<TableOperateProcess> processQueue = new PriorityQueue<TableOperateProcess>(20, TABLE_OPERATE_PROCESS_COMPARATOR);

    public ProcessManager(int size) {
        for (int i = 0; i < size; i++) {
            TableOperateProcess process = new TableOperateProcess();
            DDZExecutor.longWorker().execute(process);
            processQueue.add(process);
        }
    }

    public synchronized TableOperateProcess registerProcessListener(int tableId, TableOperateListener listener) {
        TableOperateProcess poll = processQueue.poll();
        poll.addListener(tableId, listener);
        processQueue.add(poll);
        return poll;
    }

    public synchronized void unregisterProcessListener(int tableId, TableOperateProcess process) {
        process.removeListener(tableId);
        processQueue.remove(process);
    }
}
