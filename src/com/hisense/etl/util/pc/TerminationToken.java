package com.hisense.etl.util.pc;

import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken {

    protected volatile boolean toShutdown=false;
    public final AtomicInteger reservations=new AtomicInteger(0);


    public boolean isToShutdown(){
        return toShutdown;
    }
    public void setToShutdown(boolean toShutdown){
        this.toShutdown=toShutdown;
    }
}
