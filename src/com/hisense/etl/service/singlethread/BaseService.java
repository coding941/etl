package com.hisense.etl.service.singlethread;

import com.hisense.etl.bean.AttriBaseBean;
import com.hisense.etl.bean.ContentBaseBean;
import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.bean.StudentCollegeContMetaData;
import com.hisense.etl.dao.impl.BaseDao4EsImpl;
import com.hisense.etl.service.worker.ConsumerAgent;
import com.hisense.etl.service.worker.ProducerAgent;
import com.hisense.etl.service.worker.TerminationToken;
import com.hisense.etl.util.CheckFileTypeUtil;
import com.hisense.etl.util.FileOperatorUtil;
import com.monitorjbl.xlsx.StreamingReader;
import io.netty.util.internal.StringUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;

public class BaseService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseService.class);
    private static final float THREAD_FACTOR=0.5f;
    private static final int PRODUCER_THREAD_SIZE=(int)(Runtime.getRuntime().availableProcessors()*THREAD_FACTOR);

    public BaseService(){
        super();
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
                        bean.setItemEngName(readCellValue(row.getCell(3)));
                        bean.setItemDataType(readCellValue(row.getCell(4)));
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
        String tmp=readCellValue(cell),indexs[];
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

    public static String readCellValue(final Cell cell){
        NumberFormat nf = NumberFormat.getInstance();
        if(cell!=null){
            switch (cell.getCellTypeEnum()){
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return nf.format(cell.getNumericCellValue()).replace(",", "");
                case BOOLEAN:
                    return cell.getBooleanCellValue()+"";
                case BLANK:
                    return "";
                default:
                    return cell.toString();
            }
        }
        return "";
    }

    /**
     * 增加索引公共字段，参考情报数据平台接口文档3.1
     * @return List<AttriBaseBean>
     */
    private List<AttriBaseBean> indexSharedColumn(){
        List<AttriBaseBean> ret=new ArrayList<AttriBaseBean>();
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
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_dept");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_name");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("collect_time");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("data_type");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("import_time");
        bean.setItemDataType("text");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("report_dept");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("modify_name");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("modify_time");
        bean.setItemDataType("keyword");
        ret.add(bean);
        bean=new AttriBaseBean();
        bean.setItemEngName("doc_name");
        bean.setItemDataType("keyword");
        ret.add(bean);
        return ret;
    }
    public void parseFileListFromExcel(final File file){
//        List<StudentCollegeContMetaData> list=new ArrayList<StudentCollegeContMetaData>(16);
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
                        bean.setFileName(readCellValue(row.getCell(columnNo++)));
                        bean.setFileAbsolutePath(readCellValue(row.getCell(columnNo++)));
                        bean.setOrganizationName(readCellValue(row.getCell(columnNo++)));
                        bean.setOrganizationCode(readCellValue(row.getCell(columnNo++)));
                        bean.setRecorderCode(readCellValue(row.getCell(columnNo++)));
                        bean.setRecordDate(readCellValue(row.getCell(columnNo++)));
                        bean.setExcelType(readCellValue(row.getCell(columnNo++)));
                        bean.setRecordUploadDate(readCellValue(row.getCell(columnNo++)));
                        bean.setUploadOrg(readCellValue(row.getCell(columnNo++)));
                        bean.setReviserCode(readCellValue(row.getCell(columnNo++)));
                        bean.setReviseDate(readCellValue(row.getCell(columnNo++)));
                        bean.setDataItems(readCellValue(row.getCell(columnNo++)));
                        bean.setDataArea(readCellValue(row.getCell(columnNo++)));
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
            FutureTask<Integer> ft = new FutureTask<Integer>(new ExcelTask(contList.get(i),attrList,dao));
            taskList.add(ft);
            exec.submit(ft);
        }
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Integer totalResult = 0;
//        for (FutureTask<Integer> ft : taskList) {
//            try {
//                ft.get();                //get() method is blocked.
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
        exec.shutdown();
    }

    public static void main(String[] ar){
        if(ar.length>0){
            String fileName=ar[0].replaceAll("%20"," ");
            if(CheckFileTypeUtil.belong2Excel(fileName)){
                BaseService serv=new BaseService();
                serv.parseFileListFromExcel(new File(fileName));
                serv.parseMappingFromExcel(new File(fileName));
                BaseDao4EsImpl indexOpe=new BaseDao4EsImpl(serv.getIndexName(),serv.getTypeName());
                List<AttriBaseBean> tmp=new ArrayList<AttriBaseBean>();
                tmp.addAll(serv.getAttrList());tmp.addAll(serv.getCommonAttrList());
                indexOpe.createIndex(tmp);
                serv.importExcel(indexOpe);
            }else{
                LOG.error("参数无效，该文件不是Excel文件:"+fileName);
            }

        }else{
            LOG.error("缺少参数，没有文件名");
        }

    }

    private class ExcelTask implements Callable<Integer> {

        private ContentBaseBean contMetaData;
        private List<AttriBaseBean> attrList;
        private BaseDao4EsImpl dao4Es;
        private BlockingQueue<Map<String,Object>> blockingQueue;

        public ExcelTask(ContentBaseBean contMetaData, List<AttriBaseBean> attrList, BaseDao4EsImpl dao4Es){
            this.attrList=attrList;
            this.contMetaData=contMetaData;
            this.dao4Es=dao4Es;
        }

        @Override
        public Integer call(){
            String[] items =contMetaData.getDataItems().split(",");
            if(items.length>0){
                LOG.info("task begin.");
                File file = new File(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName());
                if(CheckFileTypeUtil.belong2FileType("xlsx",file.getName()) && file.length()>4*1024*1024){
                    readXssfAsStream(items,file);
                }else{
                    readFileAsUserModel(items,file);
                }
            }
            return 1;
        }
        private void readFileAsUserModel(String[] items,File file){
            try{
                Workbook workbook = WorkbookFactory.create(file);
                if(workbook!=null){
                    long beginTime=System.currentTimeMillis();
                    //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                    //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                    int sheetNum = workbook.getNumberOfSheets();
                    int len=0,rowOffset=0,columnOffset=0,columnOffsetAfter=0,startRow,endRow,startColumn,endColumn;
                    String[] areaNum;
//                        StringBuffer rowContent=new StringBuffer("");
                    if(validDataArea(contMetaData.getDataArea())){
                        for(int i = 0; i < sheetNum; i++){
                            Sheet sheet = workbook.getSheetAt(i);
//                            firstRowNum = sheet.getFirstRowNum();
//                            lastRowNum = sheet.getLastRowNum();
                            Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();
                            if(contMetaData.getDataArea().indexOf(';')>0){
                                for(String s:contMetaData.getDataArea().split(";")) {
                                    len+=parseDataArea(s,sheet,items);
                                }
                            }else{
                                len+=parseDataArea(contMetaData.getDataArea(),sheet,items);
                            }

                        }
                    }else{
                        LOG.warn("文件数据有效区域定义有误：结束行小于起始行。"+(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName()));
                    }

//                    after workbook
                    LOG.info("file has been parsed:"+(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName()));
                    LOG.info("imported records are "+len+";elapsed time:"+(System.currentTimeMillis()-beginTime)+"ms.");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         *解析有效数据区域并放入缓存
         * @param dataArea  无分隔的有效数据区域
         * @param sheet
         * @param items
         * @throws InterruptedException
         */
        private int parseDataArea(String dataArea,Sheet sheet,String[] items){
            int len=0,rowOffset=0,columnOffset=0,columnOffsetBefore=0,startRow,endRow,startColumn,endColumn;
            String[] areaNum=dataArea.split(",");
            startRow=Integer.parseInt(areaNum[0]);
            startColumn=Integer.parseInt(areaNum[1]);
            endRow=Integer.parseInt(areaNum[2]);
            endColumn=Integer.parseInt(areaNum[3]);
            Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();
            Map<String,Object> tmp;
            for(int j=startRow;j<=endRow;j++){
                row = sheet.getRow(j-1);
                if(row!=null){
                    if(isBlankRow(row,startColumn,endColumn))continue;
                    columnOffsetBefore=0;
                    tmp=new Hashtable<String,Object>(40);
                    for(int k=startColumn;k<endColumn && k<items.length && k<row.getLastCellNum();k++){
                        tmp.put(attrList.get(Integer.parseInt(items[columnOffsetBefore].trim())-1).getItemEngName(), FileOperatorUtil.readCellValue(row.getCell(k-1)));
                        columnOffsetBefore++;
                    }
                    if(tmp.keySet().size()>0){
//                      增加索引公共字段
                        tmp.putAll(indexCommonColumn(contMetaData));
                        dao4Es.saveFromMap(tmp);
                        len++;
                    }

                }
            }
            return len;
        }

        /**
         * 验证有效区域的参数是否有效,dataArea=[row1,col1,row2,col2;row1,col3,row2,col4;row3,col1,row4,col2]
         * @param dataArea
         * @return offset of row and column begins from 1,end should be bigger than start.
         */
        private boolean validDataArea(String dataArea){
            String[] areaNum;int startRow=0,startColumn=0,endRow=0,endColumn=0;
            if(StringUtil.isNullOrEmpty(dataArea))return false;
            if(dataArea.indexOf(';')>0)return false;
            if(dataArea.indexOf(';')>0){
                for(String s:dataArea.split(";")){
                    areaNum=s.split(",");
                    startRow=Integer.parseInt(areaNum[0]);
                    startColumn=Integer.parseInt(areaNum[1]);
                    endRow=Integer.parseInt(areaNum[2]);
                    endColumn=Integer.parseInt(areaNum[3]);
                    if(startRow<=0 || startColumn<=0 || endRow<startRow || endColumn<startColumn)return false;
                }
            }else{
                areaNum=dataArea.split(",");
                startRow=Integer.parseInt(areaNum[0]);
                startColumn=Integer.parseInt(areaNum[1]);
                endRow=Integer.parseInt(areaNum[2]);
                endColumn=Integer.parseInt(areaNum[3]);
                if(startRow<=0 || startColumn<=0 || endRow<startRow || endColumn<startColumn)return false;
            }
            return true;
        }

        /**
         * two methods:
         * 1 keyword is not null
         * 2 a majority of columns are not null
         * @param row
         * @return false either keyword is content or majority of columns is content.
         */
        private boolean isBlankRow(final Row row,int startColumn,int endColumn){
            if(row!=null && startColumn>0 && endColumn>0){
                if(row.getCell(startColumn)!=null&&FileOperatorUtil.readCellValue(row.getCell(startColumn)).length()>0)return false;
                for(int i=startColumn,j=0;i<endColumn;i++){
                    if(row.getCell(i)!=null&&FileOperatorUtil.readCellValue(row.getCell(i)).length()>0)j++;
                    if(i>(startColumn+endColumn)/2 && j>(endColumn-startColumn+1)/2)return false;
                }
            }
            return true;
        }

        private void readXssfAsStream(String[] items,File file) {
            Map<String,Object> tmp;
            Workbook workbook = StreamingReader.builder().rowCacheSize(2000).bufferSize(8192).open(file);
            int i=0,j=0,columnOffset=0,len=0,columnOffsetBefore=0;
            long beginTime=System.currentTimeMillis();
            for (Sheet sheet : workbook) {
                j=0;
                for (Row r : sheet) {
                    i=0;
                    tmp=new Hashtable<String,Object>(40);
                    columnOffsetBefore=0;
                    for (Cell c : r) {
                        if(!StringUtil.isNullOrEmpty(c.getStringCellValue())){
                            columnOffset=columnOffset(contMetaData.getDataArea(),j+1,i+1);
                            if(columnOffset>=0){
                                tmp.put(attrList.get(Integer.parseInt(items[columnOffsetBefore].trim())-1).getItemEngName(), FileOperatorUtil.readCellValue(r.getCell(columnOffset)));
                            }
                        }
                        columnOffsetBefore++;
                        i++;
                    }
                    if(tmp.keySet().size()>0){
//                        增加索引公共字段
                        tmp.putAll(indexCommonColumn(contMetaData));
                        len++;
                        dao4Es.saveFromMap(tmp);
//                        try {
//                            blockingQueue.put(tmp);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                    j++;
                }
            }
            LOG.info("file has been parsed:"+(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName()));
            LOG.info("imported records are "+len+";elapsed time:"+(System.currentTimeMillis()-beginTime)+"ms.");
        }

        /**
         * 根据行号列号和有效区域返回该单元格的偏移量
         * @param dataArea
         * @param rowNum
         * @param columnNum
         * @return 负数表示不在有效数据区，正数或零表示偏移量
         */
        private int columnOffset(String dataArea,int rowNum,int columnNum){
            int offset=1,startRow=0,startColumn=0,endRow=0,endColumn=0;
            String[] area,areaNum;
            if(dataArea.contains(";")){
                area=dataArea.split(";");
                for(String s:area){
                    areaNum=s.split(",");
                    startRow=Integer.parseInt(areaNum[0]);
                    startColumn=Integer.parseInt(areaNum[1]);
                    endRow=Integer.parseInt(areaNum[2]);
                    endColumn=Integer.parseInt(areaNum[3]);
                    if(rowNum>=startRow&&rowNum<=endRow&&columnNum>=startColumn&&columnNum<=endColumn){
                        offset = columnNum-startColumn;
                        break;
                    }
                }
            }else{
                areaNum=dataArea.split(",");
                startRow=Integer.parseInt(areaNum[0]);
                startColumn=Integer.parseInt(areaNum[1]);
                endRow=Integer.parseInt(areaNum[2]);
                endColumn=Integer.parseInt(areaNum[3]);
                if(rowNum>=startRow&&rowNum<=endRow&&columnNum>=startColumn&&columnNum<=endColumn){
                    offset = columnNum-startColumn;
                }else{
                    offset = -1;
                }
            }
            return offset;
        }

        /**
         * 增加索引公共字段
         * @param contMetaData
         * @return Map<String,Object>
         */
        private Map<String,Object> indexCommonColumn(ContentBaseBean contMetaData){
            Map<String,Object> ret=new Hashtable<String,Object>(16);
//            ret.put("organizationCode",contMetaData.getOrganizationCode());
//            ret.put("organizationName",contMetaData.getOrganizationName());
//            ret.put("recorderCode",contMetaData.getRecorderCode());
//            ret.put("recordDate",contMetaData.getRecordDate());
//            ret.put("excelType",contMetaData.getExcelType());
//            ret.put("recordUploadDate",contMetaData.getRecordUploadDate());
//            ret.put("uploadOrg",contMetaData.getUploadOrg());
//            ret.put("reviserCode",contMetaData.getReviserCode());
//            ret.put("reviseDate",contMetaData.getReviseDate());
//            ret.put("fileName",contMetaData.getFileName());
            ret.put("dept_code",contMetaData.getOrganizationCode());
            ret.put("collect_dept",contMetaData.getOrganizationName());
            ret.put("collect_name",contMetaData.getRecorderCode());
            ret.put("import_time",contMetaData.getRecordDate());
            ret.put("data_type",contMetaData.getExcelType());
            ret.put("collect_time",contMetaData.getRecordUploadDate());
            ret.put("report_dept",contMetaData.getUploadOrg());
            ret.put("modify_name",contMetaData.getReviserCode());
            ret.put("modify_time",contMetaData.getReviseDate());
            ret.put("doc_name",contMetaData.getFileName());
            return ret;
        }
    }

}
