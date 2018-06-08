package com.hisense.etl.service.worker;

import com.hisense.etl.dao.impl.BaseDao4EsImpl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ConsumerAgentUnit implements Runnable {
    private BlockingQueue<Map<String,Object>> blockingQueue;
    private BaseDao4EsImpl dao4Es;

    public ConsumerAgentUnit(BlockingQueue<Map<String,Object>> blockingQueue, BaseDao4EsImpl dao4Es){
        this.blockingQueue=blockingQueue;
        this.dao4Es=dao4Es;
    }

    @Override
    public void run(){
        try {
            dao4Es.saveFromMap(blockingQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
