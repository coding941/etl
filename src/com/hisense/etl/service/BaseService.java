package com.hisense.etl.service;

import com.hisense.etl.bean.AttriBaseBean;
import com.hisense.etl.bean.ContentBaseBean;
import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.bean.StudentCollegeContMetaData;
import com.hisense.etl.dao.impl.BaseDao4EsImpl;
import com.hisense.etl.service.worker.ConsumerAgent;
import com.hisense.etl.service.worker.ProducerAgent;
import com.hisense.etl.service.worker.TerminationToken;
import com.hisense.etl.util.AppConstants;
import com.hisense.etl.util.FileOperatorUtil;
import com.hisense.etl.util.pc.term.Terminatable;
import io.netty.util.internal.StringUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class BaseService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);
    private static final int PRODUCER_THREAD_SIZE=(int)(Runtime.getRuntime().availableProcessors() * AppConstants.PRODUCER_CORE_SIZE_FACTOR);
    private static final int CONSUMER_THREAD_SIZE=(int)(Runtime.getRuntime().availableProcessors() * AppConstants.CONSUMER_CORE_SIZE_FACTOR);
    private final TerminationToken terminationToken=new TerminationToken();

//    private BlockingQueue<Map<String,Object>> blockingQueue=new ArrayBlockingQueue<Map<String, Object>>(1<<13,false);
    private BlockingQueue<Map<String,Object>> blockingQueue;

    public BaseService(){
        super();
    }


    public BaseService(BlockingQueue<Map<String,Object>> blockingQueue){
        this.blockingQueue = blockingQueue;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private String indexName;
    private String typeName;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public List<AttriBaseBean> getAttrList() {
        return attrList;
    }

    public List<AttriBaseBean> getCommonAttrList() {
        return commonAttrList;
    }

    private List<AttriBaseBean> commonAttrList=new CopyOnWriteArrayList<AttriBaseBean>();
    private List<AttriBaseBean> attrList=new CopyOnWriteArrayList<AttriBaseBean>();
    private List<ContentBaseBean> contList=new CopyOnWriteArrayList<ContentBaseBean>();

    public List<AttriBaseBean> parseMappingFromExcel(final File file){
        try {
            Workbook workbook = WorkbookFactory.create(file);
            StudentCollegeAttrMetaData bean;
            if(workbook!=null){
                //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                int sheetNum = workbook.getNumberOfSheets();
                int rowOffset=3,columnOffset=3,firstRowNum,lastRowNum,lastColumnNum;
                StringBuffer rowContent=new StringBuffer("");

                Sheet sheet = workbook.getSheetAt(0);
//                firstRowNum = sheet.getFirstRowNum();
                lastRowNum = sheet.getLastRowNum();
                lastRowNum = sheet.getPhysicalNumberOfRows();
//                 parse indexName & typeName from cell(2,4)
                initIndexNamefromExcel(sheet.getRow(2).getCell(4));
//                公共字段不作映射
                commonAttrList.addAll(indexSharedColumn());
//                解析sheet<字段描述>，获取英文名称及其在ES中的数据类型，从第四行第四列开始
                Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();
                for(int j=rowOffset;j<=lastRowNum;j++){
                    bean=new StudentCollegeAttrMetaData();
                    row = sheet.getRow(j);
                    if(null!=row){
                        lastColumnNum = row.getLastCellNum();
                        rowContent.delete(0,rowContent.length());
//                    解析第四五列(字段名称、字段数据类型)
                        bean.setItemEngName(FileOperatorUtil.readCellValue(row.getCell(3)));
                        bean.setItemDataType(FileOperatorUtil.readCellValue(row.getCell(4)));
                        if(StringUtil.isNullOrEmpty(bean.getItemEngName())){
                            break;
                        }else{
                            attrList.add(bean);
                        }

                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return attrList;
    }

    private void initIndexNamefromExcel(final Cell cell){
        String tmp=FileOperatorUtil.readCellValue(cell),indexs[];
        if(tmp.length()>0){
            indexs=tmp.split(",");
            if(indexs.length>=2){
                this.setIndexName(indexs[0]);
                this.setTypeName(indexs[1]);
            }else{
                throw new IllegalArgumentException("exception while parsing index and type argument from excel.");
            }
        }else{
            throw new IllegalArgumentException("exception while parsing index and type argument from excel.");
        }

    }

    /**
     * 增加索引公共字段，参考情报数据平台接口文档3.1
     * @return List<AttriBaseBean>
     */
    private List<AttriBaseBean> indexSharedColumn(){
        List<AttriBaseBean> ret=new CopyOnWriteArrayList<AttriBaseBean>();
        AttriBaseBean bean=new AttriBaseBean();
//        bean.setItemEngName("organizationCode");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("organizationName");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("recorderCode");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("recordDate");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("excelType");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("recordUploadDate");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("uploadOrg");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("reviserCode");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("reviseDate");
//        bean.setItemDataType("text");
//        ret.add(bean);
//        bean=new AttriBaseBean();
//        bean.setItemEngName("fileName");
//        bean.setItemDataType("text");
//        ret.add(bean);
        bean.setItemEngName("dept_code");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_dept");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_name");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_time");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("data_type");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("import_time");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("report_dept");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("modify_name");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("modify_time");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("doc_name");
        bean.setItemDataType(AppConstants.COMMON_ATTRI_TYPE);
        ret.add(bean);
        return ret;
    }
    public void parseFileListFromExcel(final File file){
        try {
            Workbook workbook = WorkbookFactory.create(file);
            if(workbook!=null){
                //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                int sheetNum = workbook.getNumberOfSheets();
                int rowOffset=1,columnOffset=0,firstRowNum,lastRowNum,lastColumnNum,columnNo;
//                StringBuffer rowContent=new StringBuffer("");
//                解析sheet<数据文件>，缓存所有数据文件的属性
                Sheet sheet = workbook.getSheetAt(1);
                firstRowNum = sheet.getFirstRowNum();
                lastRowNum = sheet.getLastRowNum();

                Cell cell;Row row;
                for(int j=rowOffset;j<=lastRowNum;j++){
                    StudentCollegeContMetaData bean = new StudentCollegeContMetaData();
                    row = sheet.getRow(j);
//                    lastColumnNum = row.getLastCellNum();
//                    rowContent.delete(0,rowContent.length());
                    if(row!=null){
                        columnNo=0;
                        bean.setFileName(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setFileAbsolutePath(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setOrganizationName(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setOrganizationCode(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setRecorderCode(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setRecordDate(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setExcelType(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setRecordUploadDate(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setUploadOrg(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setReviserCode(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setReviseDate(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setDataItems(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        bean.setDataArea(FileOperatorUtil.readCellValue(row.getCell(columnNo++)));
                        if(StringUtil.isNullOrEmpty(bean.getFileName()) || StringUtil.isNullOrEmpty(bean.getFileAbsolutePath()) || StringUtil.isNullOrEmpty(bean.getDataItems()) || StringUtil.isNullOrEmpty(bean.getDataArea())){
                            LOG.warn("文件名称不能为空，文件路径不能为空，数据项不能为空，数据有效区域不能为空。");
                            break;
//                        throw new IllegalArgumentException("索引Excel文件"+file.getName()+"数据异常.");
                        }else{
                            contList.add(bean);
                        }

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
//        return contList;
    }
    public void importExcel(final BaseDao4EsImpl dao){
        List<FutureTask<Integer>> taskList = new ArrayList<FutureTask<Integer>>();
        ExecutorService exec = Executors.newFixedThreadPool(PRODUCER_THREAD_SIZE);
        for (int i = 0; i < contList.size(); i++) {
            // 传入Callable对象创建FutureTask对象
            FutureTask<Integer> ft = new FutureTask<Integer>(new ProducerAgent(contList.get(i),attrList,blockingQueue));
            exec.submit(ft);
            taskList.add(ft);
        }
//        try {
//            Thread.sleep(1000l);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ExecutorService exec4Es = Executors.newFixedThreadPool(CONSUMER_THREAD_SIZE);
        for(int i=0;i<CONSUMER_THREAD_SIZE;i++){
            new Thread(new ConsumerAgent(blockingQueue,dao)).start();
        }
        Integer totalResult = 0;
        for (FutureTask<Integer> ft : taskList) {
            try {
                ft.get();                //get() method is blocked.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
//        shutdown();
    }

    private void shutdown(){
//        while(true){
//            if(TerminationToken.reservations.get()<=0){
//                terminationToken.setToShutdown(true);
//                break;
//            }
//            try {
//                Thread.sleep(500l);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    public static void main(String[] ar){
        BaseService serv=new BaseService();
//        String fileName="C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\数据说明\\tmp\\高校学生信息.xlsx";
//        serv.parseFileListFromExcel(new File(fileName));
//        serv.parseMappingFromExcel(new File(fileName));
//        BaseDao4EsImpl indexOpe=new BaseDao4EsImpl(serv.getIndexName(),serv.getTypeName());
//        indexOpe.createIndex(serv.attrList);
//        serv.importExcel();
        serv.indexSharedColumn();
    }



}
