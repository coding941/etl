package com.hisense.etl.util;

import com.hisense.etl.dao.impl.BaseDao4EsImpl;
import com.hisense.etl.service.BaseService;
import com.hisense.etl.service.worker.ConsumerAgent;
import com.hisense.etl.service.worker.TerminationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StrUtil {

    private BlockingQueue<Map<String,Object>> blockingQueue = new ArrayBlockingQueue<Map<String,Object>>(1<<13,false);
    private String indexName="command_aaa",typeName="info";
    private final int core_size=128;
    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);
    private long insertDataTime;

    private void init(){
//        cache data
        long cacheDataTime=System.currentTimeMillis();
        Map<String,Object> tmp=new Hashtable<String,Object>(40);
        for(int i=1;i<4805;i++){
            tmp=new Hashtable<String,Object>();
            tmp.put("data_type",""+i);
            tmp.put("dept_code","370211000000");
            tmp.put("politicalStatus","团员");
            tmp.put("className","数学171");
            tmp.put("idCard","360421199810264013");
            tmp.put("collect_dept","开发区分局");
            tmp.put("modify_time","2018-05-09 16:30:26");
            tmp.put("enrollmentDate","9/2/2017 0:0:0");
            tmp.put("accommodationCampus","黄岛（嘉陵江路）校区");
            tmp.put("collect_name","admin");
            tmp.put("professionName","数学与应用数学");
            tmp.put("academyName","理学院");
            tmp.put("cultureLevel","普通本科生");
            tmp.put("name","胡津林");
            tmp.put("collect_time","2017-10-31 00:00:00");

            tmp.put("doc_name","2017黄岛学生原籍数据.xlsx");
            tmp.put("expectedGraduateDate","2021");
            tmp.put("collegePeriod","四年");
            tmp.put("grade","2017");
            tmp.put("housePhone","17864296097");
            tmp.put("nativePlace","山东省海阳市留格庄镇后望海村");
            tmp.put("import_time","2018-05-09 16:30:26");
            tmp.put("studentId","201701071");
            tmp.put("houseAddress","山东省海阳市核电区大辛家村");
            tmp.put("houseDistrictName","山东");
            tmp.put("gender","男");
            tmp.put("report_dept","青岛理工大学");
            tmp.put("modify_name","admin");

            try {
                blockingQueue.put(tmp);
                TerminationToken.reservations.incrementAndGet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOG.info("cache data time:"+(System.currentTimeMillis()-cacheDataTime)+"ms.");
//        delete data of index
        long deleteDataTime=System.currentTimeMillis();
        new IndexOperator().deleteByQuery(indexName,typeName);
        LOG.info("delete data time:"+(System.currentTimeMillis()-deleteDataTime)+"ms.");
//        insert data into index
        insertDataTime = System.currentTimeMillis();
        LOG.info("insert data begin time:"+(insertDataTime));
        for(int i=0;i<core_size;i++){
            new Thread(new ConsumerAgentUnit(blockingQueue,new BaseDao4EsImpl(indexName,typeName))).start();
        }
//        while (true){
//            if(TerminationToken.reservations.get()<0){
//                LOG.info("insert data time:"+(System.currentTimeMillis()-insertDataTime)+"ms.");
//                break;
//            }
//        }
    }
    public static void main(String[] ar){
        new StrUtil().init();
    }

    private class ConsumerAgentUnit implements Runnable {
        private BlockingQueue<Map<String,Object>> blockingQueue;
        private BaseDao4EsImpl dao4Es;

        public ConsumerAgentUnit(BlockingQueue<Map<String,Object>> blockingQueue, BaseDao4EsImpl dao4Es){
            this.blockingQueue=blockingQueue;
            this.dao4Es=dao4Es;
        }

        @Override
        public void run(){
            try {
                while(true){
                    if(TerminationToken.reservations.decrementAndGet()<0){
                        System.out.println("thread end:"+System.currentTimeMillis()+";insert time:"+(System.currentTimeMillis()-insertDataTime)+"ms.");
                        break;
                    }
                    dao4Es.saveFromMap(blockingQueue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
