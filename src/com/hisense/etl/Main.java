package com.hisense.etl;

import com.hisense.etl.bean.AttriBaseBean;
import com.hisense.etl.dao.impl.BaseDao4EsImpl;
import com.hisense.etl.service.BaseService;
import com.hisense.etl.service.worker.TerminationToken;
import com.hisense.etl.util.AppConstants;
import com.hisense.etl.util.CheckFileTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);

    public static void main(String[] args) {
        if(args.length>0){
            String fileName=args[0].replaceAll("%20"," ");
            if(CheckFileTypeUtil.belong2Excel(fileName)){
                long beginTime=System.currentTimeMillis();
                LOG.info("system begin:"+beginTime);
                BlockingQueue<Map<String,Object>> blockingQueue=new ArrayBlockingQueue<Map<String, Object>>(AppConstants.BLOCK_CACHE_SIZE,false);
                BaseService serv=new BaseService(blockingQueue);
                serv.parseMappingFromExcel(new File(fileName));
                serv.parseFileListFromExcel(new File(fileName));
                BaseDao4EsImpl indexOpe=new BaseDao4EsImpl(serv.getIndexName(),serv.getTypeName());
                List<AttriBaseBean> tmp=new CopyOnWriteArrayList<AttriBaseBean>();
                tmp.addAll(serv.getAttrList());tmp.addAll(serv.getCommonAttrList());
                indexOpe.createIndex(tmp);
                serv.importExcel(indexOpe);
                while (true){
                    if(TerminationToken.reservations.get()<0){
                        LOG.info("time elapsed:"+(System.currentTimeMillis()-beginTime)+"ms.");
                        break;
                    }
                }

            }else{
                LOG.error("参数无效，该文件不是Excel文件:"+fileName);
            }
        }else{
            LOG.error("缺少参数，没有文件名");
        }
//        LOG.info("available processor:"+Runtime.getRuntime().availableProcessors());
    }
}
