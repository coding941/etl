package com.hisense.etl.service.worker;

import com.hisense.etl.bean.AttriBaseBean;
import com.hisense.etl.bean.ContentBaseBean;
import com.hisense.etl.service.BaseService;
import com.hisense.etl.util.CheckFileTypeUtil;
import com.hisense.etl.util.FileOperatorUtil;
import com.monitorjbl.xlsx.StreamingReader;
import io.netty.util.internal.StringUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class ProducerAgent implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerAgent.class);

    private ContentBaseBean contMetaData;
    private List<AttriBaseBean> attrList;
    private BlockingQueue<Map<String,Object>> blockingQueue;

    public ProducerAgent(ContentBaseBean contMetaData, List<AttriBaseBean> attrList, BlockingQueue<Map<String,Object>> blockingQueue){
        this.attrList=attrList;
        this.contMetaData=contMetaData;
        this.blockingQueue=blockingQueue;
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
        Map<String,Object> tmp=new Hashtable<String,Object>();
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
                        if(contMetaData.getDataArea().indexOf(';')>0){
                            for(String s:contMetaData.getDataArea().split(";")){
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
//                                增加索引公共字段
                    tmp.putAll(indexCommonColumn(contMetaData));
//                            dao4Es.saveFromMap(tmp);
                    try {
                        blockingQueue.put(tmp);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        LOG.warn("缓存数据失败，任务被中断。");
                    }
                    TerminationToken.reservations.incrementAndGet();
                    len++;
                }

            }
        }
        return len;
    }

    /**
     * two methods:
     * 1 keyword is not null
     * 2 majority of column is not null
     * @param row
     * @return false either keyword has content or majority of column has content.
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

    /**
     * 流式读取只支持单个有效区域
     * 不支持多个有效数据区域
     *
     * @param items
     * @param file
     */
    private void readXssfAsStream(String[] items,File file) {
        Map<String,Object> tmp=new Hashtable<String,Object>();
        Workbook workbook = StreamingReader.builder().rowCacheSize(2000).bufferSize(8192).open(file);
        int i=0,j=0,columnOffset=0,len=0,startRow=0,startColumn=0,endRow=0,endColumn=0;
        long beginTime=System.currentTimeMillis();
        if(validDataArea(contMetaData.getDataArea())){
            for (Sheet sheet : workbook) {
                if(contMetaData.getDataArea().indexOf(';')>0){
                    LOG.warn("在流式读取中不支持多个有效数据区域.");
                }else{
                    String[] areaNum=contMetaData.getDataArea().split(",");
                    startRow=Integer.parseInt(areaNum[0]);
                    startColumn=Integer.parseInt(areaNum[1]);
                    endRow=Integer.parseInt(areaNum[2]);
                    endColumn=Integer.parseInt(areaNum[3]);
                    j=0;
                    for (Row r : sheet) {
                        i=0;
                        if((j+1)<startRow){
                            j++;
                            continue;
                        }
                        if((j+1)>endRow)break;
                        if(isBlankRow4Stream(r,startColumn,endColumn))continue;
                        tmp=new Hashtable<String,Object>(40);
                        for (Cell c : r) {
                            if((i+1)<startColumn){
                                i++;
                                continue;
                            }
                            if((i+1)>endColumn)break;
                            if(!StringUtil.isNullOrEmpty(c.getStringCellValue())){
//                                columnOffset=columnOffset(contMetaData.getDataArea(),j+1,i+1);
                                tmp.put(attrList.get(Integer.parseInt(items[i].trim())-1).getItemEngName(), FileOperatorUtil.readCellValue(c));
                            }
                            i++;
                        }
                        if(tmp.keySet().size()>0){
//                        增加索引公共字段
                            tmp.putAll(indexCommonColumn(contMetaData));
                            try {
                                blockingQueue.put(tmp);
                                TerminationToken.reservations.incrementAndGet();
                                len++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        j++;
                    }

                }
            }

        }
        LOG.info("file has been parsed:"+(contMetaData.getFileAbsolutePath()+System.getProperty("file.separator")+contMetaData.getFileName()));
        LOG.info("imported records are "+len+";elapsed time:"+(System.currentTimeMillis()-beginTime)+"ms.");
    }
    /**
     * two methods:
     * 1 keyword is not null
     * 2 a majority of columns are not null
     * @param row
     * @return false either keyword is content or a majority of columns are content.
     */
    private boolean isBlankRow4Stream(Row row,int startColumn,int endColumn){
        if(row!=null && startColumn>0 && endColumn>0){
            int i=0,j=0;
            for(Cell c:row){
//                keyword is not null
                if((i+1)<startColumn){
                    i++;
                    continue;
                }
                if((i+1)==startColumn&&c!=null&&FileOperatorUtil.readCellValue(c).length()>0)return false;
//                a majority of columns are not null
                if((i+1)>=startColumn && (i+1)<=endColumn){
                    if(c!=null&&FileOperatorUtil.readCellValue(c).length()>0)j++;
                    if(i>(startColumn+endColumn)/2 && j>(endColumn-startColumn+1)/2)return false;
                }
                if((i+1)>endColumn)break;
                i++;
            }
        }
        return true;
    }
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
                if(endRow<startRow || endColumn<startColumn)return false;
            }
        }else{
            areaNum=dataArea.split(",");
            startRow=Integer.parseInt(areaNum[0]);
            startColumn=Integer.parseInt(areaNum[1]);
            endRow=Integer.parseInt(areaNum[2]);
            endColumn=Integer.parseInt(areaNum[3]);
            if(endRow<startRow || endColumn<startColumn)return false;
        }
        return true;
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
        Map<String,Object> ret=new ConcurrentHashMap<String,Object>();
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
