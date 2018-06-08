package com.hisense.etl.service.worker;

import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {
    protected volatile boolean toShutdown=false;
    public final static AtomicInteger reservations=new AtomicInteger(0);

    public boolean isToShutdown(){
        return toShutdown;
    }
    public void setToShutdown(boolean toShutdown){
        this.toShutdown=toShutdown;
    }
}
