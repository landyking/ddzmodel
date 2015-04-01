package com.jfreer.game.ddz.thread;

import com.jfreer.game.ddz.operate.TableOperate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: landy
 * Date: 15/4/1
 * Time: 下午1:51
 */
public class TableOperateProcess implements Runnable {
    private BlockingQueue<TableOperate> operateBlockingQueue = new LinkedBlockingQueue<TableOperate>();
    private ConcurrentMap<Integer, TableOperateListener> listenerMap = new ConcurrentHashMap<Integer, TableOperateListener>();

    boolean addListener(int tableId, TableOperateListener listener) {
        TableOperateListener rst = listenerMap.putIfAbsent(tableId, listener);
        return rst == null;
    }

    int listenerCount() {
        return listenerMap.size();
    }

    void removeListener(int tableId) {
        listenerMap.remove(tableId);
    }

    @Override
    public void run() {
        try {
            while (true) {
                TableOperate take = operateBlockingQueue.take();
                if (take.getDestTableId() != null && listenerMap.containsKey(take.getDestTableId())) {
                    listenerMap.get(take.getDestTableId()).process(take);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean addOperate(TableOperate operate) {
        return operateBlockingQueue.add(operate);
    }
}
