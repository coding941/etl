package com.hisense.etl.util;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileReader {

    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(FileReader.class);

    public void readXssfAsStream(File file) {

//            InputStream is = new FileInputStream(file);
        File destFile = new File("D:\\fileRepository\\" + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".txt");
        FileWriter fileWriter = null;

        StringBuffer sb = new StringBuffer("");
        Workbook workbook = StreamingReader.builder().rowCacheSize(2000).bufferSize(8192).open(file);
        try {
            if (!destFile.exists())
                destFile.createNewFile();
            fileWriter = new FileWriter(destFile, true);
            for (Sheet sheet : workbook) {
                for (Row r : sheet) {
                    sb.delete(0, sb.length());
                    for (Cell c : r) {
                        sb.append(c.getStringCellValue()).append(",");
                    }
//                logger.info(sb.toString());
                    fileWriter.write(sb.toString() + System.lineSeparator());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    }

    private void appendNewLine(String fileName,String content){
        File destFile=new File("D:\\fileRepository\\"+fileName.substring(0,fileName.lastIndexOf(".") )+".txt");
        FileWriter fileWriter=null;
        try{
            if(!destFile.exists())
                destFile.createNewFile();
            fileWriter=new FileWriter(destFile,true);
            fileWriter.write(content+System.getProperty("line.separator"));
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(fileWriter!=null)fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] ar){
        FileReader reader=new FileReader();
        long beginTime=System.currentTimeMillis();
//        reader.readXssfAsStream(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"));
        reader.readXssfAsStream(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\自来水信息\\水卡档案（西区）7.20上报\\水卡档案（海王所）.xls"));
        logger.info("parse file elasped time:"+(System.currentTimeMillis()-beginTime)+"ms.");
    }

}
