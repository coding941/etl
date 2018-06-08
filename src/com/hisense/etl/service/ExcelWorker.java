package com.hisense.etl.service;

import com.hisense.etl.bean.StudentCollegeContMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExcelWorker {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private List<StudentCollegeContMetaData> list;

    public void importExcel(){

        List<FutureTask<Integer>> taskList = new ArrayList<FutureTask<Integer>>();
        // newFixedThreadPool or newCachedThreadPool
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < list.size(); i++) {
            // 传入Callable对象创建FutureTask对象
            FutureTask<Integer> ft = new FutureTask<Integer>(new ExcelWorker().new ExcelTask());
            taskList.add(ft);
            exec.submit(ft);
        }


        // 开始统计各计算线程计算结果
        Integer totalResult = 0;
        for (FutureTask<Integer> ft : taskList) {
            try {
                //FutureTask的get方法会自动阻塞,直到获取计算结果为止
                ft.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 关闭线程池
        exec.shutdown();
        System.out.println("多任务计算后的总结果是:" + totalResult);
    }


    class ExcelTask implements Callable<Integer>{
        private StudentCollegeContMetaData contMetaData;

        @Override
        public Integer call(){

            return 1;
        }
    }
}
