package com.hisense.etl.service;

import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.bean.StudentCollegeContMetaData;
import com.hisense.etl.bean.WaterAttrMetaData;
import com.hisense.etl.bean.WaterContMetaData;
import com.hisense.etl.dao.impl.StudentCollegeDao4EsImpl;
import com.hisense.etl.dao.impl.WaterDao4EsImpl;
import com.hisense.etl.util.CheckFileTypeUtil;
import com.hisense.etl.util.EsAdminUtil;
import com.monitorjbl.xlsx.StreamingReader;
import io.netty.util.internal.StringUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WaterService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(WaterService.class);


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
    private List<WaterAttrMetaData> attrList=new ArrayList<WaterAttrMetaData>();
    private List<WaterContMetaData> contList=new ArrayList<WaterContMetaData>();

    private List<WaterAttrMetaData> parseMappingFromExcel(final File file){
        try {
            Workbook workbook = WorkbookFactory.create(file);
            WaterAttrMetaData bean;
            if(workbook!=null){
                //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                int sheetNum = workbook.getNumberOfSheets();
                int rowOffset=3,columnOffset=3,firstRowNum,lastRowNum,lastColumnNum;
                StringBuffer rowContent=new StringBuffer("");

                Sheet sheet = workbook.getSheetAt(0);
//                firstRowNum = sheet.getFirstRowNum();
                lastRowNum = sheet.getLastRowNum();
//                 parse indexName & typeName from cell(2,4)
                parseIndexNamefromExcel(sheet.getRow(2).getCell(4));
//                解析sheet<字段描述>，获取英文名称及其在ES中的数据类型，从第四行第四列开始
                Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();
                for(int j=rowOffset;j<=lastRowNum;j++){
                    bean=new WaterAttrMetaData();
                    row = sheet.getRow(j);
//                    lastColumnNum = row.getLastCellNum();
                    if(row !=null){
                        rowContent.delete(0,rowContent.length());
//                        解析第四五列(字段名称、字段数据类型)
                        bean.setItemEngName(readCellValue(row.getCell(3)));
                        bean.setItemDataType(readCellValue(row.getCell(4)));
                        if(StringUtil.isNullOrEmpty(bean.getItemEngName()) || StringUtil.isNullOrEmpty(bean.getItemDataType())){
                            LOG.info("最大行数:"+lastRowNum+";itemEngname属性英文名称不能为空,itemDataType属性数据类型不能为空.");
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

    private void parseIndexNamefromExcel(final Cell cell){
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

    private String readCellValue(final Cell cell){
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

    private void parseFileListFromExcel(final File file){
//        List<StudentCollegeContMetaData> list=new ArrayList<StudentCollegeContMetaData>(16);
        try {
            Workbook workbook = WorkbookFactory.create(file);
            if(workbook!=null){
                //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                int sheetNum = workbook.getNumberOfSheets();
                int rowOffset=1,columnOffset=0,firstRowNum,lastRowNum,lastColumnNum,columnNo;
                StringBuffer rowContent=new StringBuffer("");
//                解析sheet<数据文件>，缓存所有数据文件的属性
                Sheet sheet = workbook.getSheetAt(1);
                firstRowNum = sheet.getFirstRowNum();
                lastRowNum = sheet.getLastRowNum();

                Cell cell;Row row;
                for(int j=rowOffset;j<=lastRowNum;j++){
                    WaterContMetaData bean = new WaterContMetaData();
                    row = sheet.getRow(j);
//                    lastColumnNum = row.getLastCellNum();
                    rowContent.delete(0,rowContent.length());
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
                        LOG.info("最大行数:"+lastRowNum+";文件名称不能为空,文件路径不能为空，数据项不能为空，数据有效区域不能为空。");
//                        throw new IllegalArgumentException("索引Excel文件"+file.getName()+"数据异常.");
                    }else{
                        contList.add(bean);
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
    public void importExcel(){
        List<FutureTask<Integer>> taskList = new ArrayList<FutureTask<Integer>>();
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < contList.size(); i++) {
            // 传入Callable对象创建FutureTask对象
            FutureTask<Integer> ft = new FutureTask<Integer>(new WaterService().new ExcelTask(contList.get(i),attrList,new WaterDao4EsImpl(this.getIndexName(),this.getTypeName())));
            taskList.add(ft);
            exec.submit(ft);
        }

        Integer totalResult = 0;
        for (FutureTask<Integer> ft : taskList) {
            try {
                //get() method is blocked.
                ft.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        exec.shutdown();
    }

    public static void main(String[] ar){
        WaterService serv=new WaterService();
        String fileName="C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\数据说明\\tmp\\高校学生信息.xlsx";
        serv.parseFileListFromExcel(new File(fileName));
        serv.parseMappingFromExcel(new File(fileName));
        WaterDao4EsImpl indexOpe=new WaterDao4EsImpl(serv.getIndexName(),serv.getTypeName());
        indexOpe.createIndex(serv.attrList);
        serv.importExcel();
    }

    class ExcelTask implements Callable<Integer> {
        private WaterContMetaData contMetaData;
        private List<WaterAttrMetaData> attrList;
        private WaterDao4EsImpl<T> dao4Es;

        public ExcelTask(WaterContMetaData contMetaData,List<WaterAttrMetaData> attrList,WaterDao4EsImpl<T> dao4Es){
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
            Map<String,Object> tmp=new HashMap<String,Object>();
            try{
                Workbook workbook = WorkbookFactory.create(file);
                if(workbook!=null){
                    long beginTime=System.currentTimeMillis();
                    //        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
                    //        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                    int sheetNum = workbook.getNumberOfSheets();
                    int len=0;
                    int rowOffset=0,columnOffset=0,firstRowNum,lastRowNum,lastColumnNum;
//                        StringBuffer rowContent=new StringBuffer("");
                    for(int i = 0; i < sheetNum; i++){
                        Sheet sheet = workbook.getSheetAt(i);
                        firstRowNum = sheet.getFirstRowNum();
                        lastRowNum = sheet.getLastRowNum();
                        len+=lastRowNum-firstRowNum;
                        Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();

                        for(int j=rowOffset;j<=lastRowNum;j++){
                            row = sheet.getRow(j);
                            lastColumnNum = row.getLastCellNum();
                            tmp.clear();
                            for(int k=columnOffset;k<lastColumnNum && k<items.length;k++){
//                                attribute items code is from 1 & items index from 0
//                                valid area ,row first
                                tmp.put(attrList.get(Integer.parseInt(items[k].trim())-1).getItemEngName(),readCellValue(row.getCell(k)));
                            }
                            dao4Es.saveFromMap(tmp);
                        }
                    }
//                    after workbook
                    LOG.info("file has been parsed:"+(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName()));
                    LOG.info("imported records are "+len+";elapsed time:"+(System.currentTimeMillis()-beginTime)+"ms.");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void readXssfAsStream(String[] items,File file) {
            Map<String,Object> tmp=new HashMap<String,Object>();
            Workbook workbook = StreamingReader.builder().rowCacheSize(2000).bufferSize(8192).open(file);
            int i=0,j=0;
            for (Sheet sheet : workbook) {
                for (Row r : sheet) {
                    i=0;
                    for (Cell c : r) {
                        if(!StringUtil.isNullOrEmpty(c.getStringCellValue())){
                            tmp.put(attrList.get(Integer.parseInt(items[i].trim())-1).getItemEngName(),readCellValue(r.getCell(i)));
                        }
                        i++;
                    }
                    dao4Es.saveFromMap(tmp);
                    j++;
                }
            }

        }

        /**
         * 根据行号列号和有效区域返回该单元格的偏移量
         * @param items
         * @param dataArea
         * @param columnNum
         * @return 负数表示不在有效数据区，正数或零表示偏移量
         */
        private int columnOffset(String[] items,String dataArea,int rowNum,int columnNum){
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
                areaNum=dataArea.split(";");
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
    }
}
