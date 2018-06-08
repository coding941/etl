package com.hisense.etl.service;

import com.hisense.etl.dao.impl.StudentCollegeDao4EsImpl;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileHandler {
    private static int consumer_thread_size=2;

    public static void startHandle(File root){
        BlockingQueue<File> fileBlockingQueue=new ArrayBlockingQueue<File>(16);
        final FileFilter fileFilter=new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileName = pathname.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                if("xls".equalsIgnoreCase(suffix) || "xlsx".equalsIgnoreCase(suffix)){
                    return true;
                }
                return false;
            }
        };
        new Thread(new FileCrawler(fileBlockingQueue,fileFilter,root)).start();
        for (int i=0;i<consumer_thread_size;i++)
            new Thread(new ExcelHandler(new StudentCollegeDao4EsImpl("",""),fileBlockingQueue)).start();
    }

    public static void main(String[] ar){
        FileHandler.startHandle(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\高校新生信息2017"));
    }


}
