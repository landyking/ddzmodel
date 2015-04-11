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
    private final int id;
    private BlockingQueue<TableOperate> operateBlockingQueue = new LinkedBlockingQueue<TableOperate>();
    private ConcurrentMap<Integer, TableOperateListener> listenerMap = new ConcurrentHashMap<Integer, TableOperateListener>();

    public TableOperateProcess(int i) {
        this.id=i;
    }

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
            Thread.currentThread().setName("table-process-"+id);
            while (true) {
                TableOperate take = operateBlockingQueue.take();
                long tmp=System.currentTimeMillis();
                if (take.getDestTableId() != null && listenerMap.containsKey(take.getDestTableId())) {
                    TableOperateListener listener = listenerMap.get(take.getDestTableId());
                    try {
                        listener.process(take);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                long offset=System.currentTimeMillis()-tmp;
                //System.out.println(offset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addOperate(TableOperate operate) {
        return operateBlockingQueue.add(operate);
    }
}
