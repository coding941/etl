package com.hisense.etl.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Indexer implements Runnable {
    private final BlockingQueue<File> queue;

    public Indexer(BlockingQueue<File> queue){
        this.queue=queue;
    }

    @Override
    public void run(){
        try{
            while(true){
                indexFile(queue.take());
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * this action should be thread-safe.
     * @param file
     */
    private void indexFile(File file){
        File destFile=new File("D:\\fileRepository\\file_list.txt");
        FileWriter fileWriter=null;
        try{
            if(!destFile.exists())
                destFile.createNewFile();
            fileWriter=new FileWriter(destFile,true);
            fileWriter.write(System.currentTimeMillis()+","+file.getAbsolutePath()+";"+System.getProperty("line.separator"));
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(fileWriter!=null)fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
