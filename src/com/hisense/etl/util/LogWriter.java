package com.hisense.etl.util;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * producer-consumer logging service with no shutdown support
 */
public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private static int capaci;

    public LogWriter(Writer writer){
        this.queue=new LinkedBlockingDeque<String>(capaci);
        this.logger=new LoggerThread(writer);
    }

    public void start(){
        logger.start();
    }

    public void log(String msg) throws InterruptedException{
        queue.put(msg);
    }

    private class LoggerThread extends Thread{
        private final PrintWriter writer;
        public LoggerThread(Writer writer){
            this.writer=(PrintWriter)writer;
        }
        @Override
        public void run(){

            try{
                while(true){
                    writer.println(queue.take());
                }
            }catch(InterruptedException ign){

            }finally {
                writer.close();
            }
        }
    }
}
