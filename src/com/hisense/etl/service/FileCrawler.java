package com.hisense.etl.service;

import com.hisense.etl.util.CheckFileTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

public class FileCrawler implements Runnable {

    private static final Logger logger= LoggerFactory.getLogger(Class.class);

    private final BlockingQueue<File> fileBlockingQueue;
    private final FileFilter fileFilter;
    private final File root;

    public FileCrawler(BlockingQueue<File> fileBlockingQueue,FileFilter fileFilter,File root){
        this.fileBlockingQueue=fileBlockingQueue;
        this.fileFilter=fileFilter;
        this.root=root;
    }

    @Override
    public void run(){
        try{
            crawl(root);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void crawl(File root) throws InterruptedException{
        File[] entries = root.listFiles();
        if(entries!=null){
            for (File f:entries){
                if(f.isDirectory()){
                    crawl(f);
                }else if((!alreadyIndexed(f)) && (CheckFileTypeUtil.belong2FileType("xls",f.getName()) || CheckFileTypeUtil.belong2FileType("xlsx",f.getName()))){
                    fileBlockingQueue.put(f);
                }
            }
        }
    }


    private boolean alreadyIndexed(File file){
        logger.info(file.getAbsolutePath());
        return false;
    }

}
