package com.hisense.etl.service;

import com.hisense.etl.dao.impl.StudentCollegeDao4EsImpl;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ExcelHandler<T> implements Runnable{
    private static int consumer_thread_size=2;
    private StudentCollegeDao4EsImpl<T> dao4Es;
    private BlockingQueue<File> fileBlockingQueue;
    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(Class.class);

    public ExcelHandler(StudentCollegeDao4EsImpl<T> dao4Es,BlockingQueue<File> fileBlockingQueue){
        this.dao4Es=dao4Es;
        this.fileBlockingQueue=fileBlockingQueue;
    }

    /**
     *
     * handle excel in memory tree
     * load entire file(dom) into memory
     *
     * @throws Exception
     */
    @Override
    public void run(){
        while(true){
            try {
                readContent2Es(fileBlockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readContent2Es(File file){
        try{
            Workbook workbook = WorkbookFactory.create(file);
            if(workbook!=null){
                long beginTime=System.currentTimeMillis();
//        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
                int sheetNum = workbook.getNumberOfSheets();
                int len=0;
                int rowOffset=0,columnOffset=1,firstRowNum,lastRowNum,lastColumnNum;
                StringBuffer rowContent=new StringBuffer("");
                for(int i = 0; i < sheetNum; i++){
                    Sheet sheet = workbook.getSheetAt(i);
                    firstRowNum = sheet.getFirstRowNum();
                    lastRowNum = sheet.getLastRowNum();
                    len+=lastRowNum-firstRowNum;
                    Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();

                    for(int j=rowOffset;j<=lastRowNum;j++){
                        row = sheet.getRow(j);
                        lastColumnNum = row.getLastCellNum();
                        rowContent.delete(0,rowContent.length());
                        for(int k=0;k<=lastColumnNum;k++){
                            rowContent.append(readCellValue(row.getCell(k))+",");
                        }
                        dao4Es.saveFromMap(toMap(rowContent.toString()));
//                    appendNewLine(file.getName(),rowContent.toString());
                    }
                }
//                after workbook
                logger.info("file has been parsed:"+file.getAbsolutePath());
                logger.info("imported records are "+len+";elapsed time:"+(System.currentTimeMillis()-beginTime)+"ms.");
            }

        }catch (Exception e){
            e.printStackTrace();
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

    private Map<String, Object> toMap(String bean){
        Map<String,Object> ret=new HashMap<String,Object>();
        String[] tmpStr=bean.split(",");
        int i=0;
        if(i<tmpStr.length)ret.put("name",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("gender",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("idcard",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("studentId",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("politicalStatus",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("healthStatus",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("birthday",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("enrollmentDate",tmpStr[i++]);

        if(i<tmpStr.length)ret.put("collegeName",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("collegeAddress",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("instituteName",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("professionName",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("expectedGraduateDate",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("accommodationCampus",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("grade",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("housePhone",tmpStr[i++]);

        if(i<tmpStr.length)ret.put("houseAddress",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("enrollmentStatus",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("professionClass",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("cultureLevel",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("houseDistrictName",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("enrollmentType",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("collegeFormality",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("collegeType",tmpStr[i++]);
        if(i<tmpStr.length)ret.put("enrollmentSeason",tmpStr[i++]);
        return ret;
    }
    public static void main(String[] ar){

    }


}
