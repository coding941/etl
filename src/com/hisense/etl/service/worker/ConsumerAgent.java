package com.hisense.etl.service.worker;

import com.hisense.etl.dao.impl.BaseDao4EsImpl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class ConsumerAgent implements Runnable {
    private BlockingQueue<Map<String,Object>> blockingQueue;
    private BaseDao4EsImpl dao4Es;
    private boolean endFlag=false;

    public ConsumerAgent(BlockingQueue<Map<String,Object>> blockingQueue,BaseDao4EsImpl dao4Es){
        this.blockingQueue=blockingQueue;
        this.dao4Es=dao4Es;
    }

    @Override
    public void run(){
        try {
            while(true){
                if(TerminationToken.reservations.decrementAndGet()<0){
                    if(!endFlag){
                        Thread.sleep(250l);
                        endFlag=true;
                    }else{
                        System.out.println("system end:"+System.currentTimeMillis());
                        break;
                    }
                }
                dao4Es.saveFromMap(blockingQueue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
