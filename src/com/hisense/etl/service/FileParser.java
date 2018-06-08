package com.hisense.etl.service;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.NumberFormat;


public class FileParser {
    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(Class.class);
    private static int consumer_thread_size=2;
    private static int large_file_size=1<<23;

    private final OPCPackage xlsxPackage;
    private final int minColumns;

    public FileParser(OPCPackage pkg,  int minColumns)
    {
        this.xlsxPackage = pkg;
        this.minColumns = minColumns;
    }

    public void parseFileAsStream(final File file)throws IOException, OpenXML4JException, SAXException {
        if(file.length()>large_file_size){
            if(validateFileTypeFromSuffix(file,"xls")){

            }else if(validateFileTypeFromSuffix(file,"xlsx")){
                ReadOnlySharedStringsTable stringsTable = new ReadOnlySharedStringsTable(this.xlsxPackage);
                XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
                StylesTable styles = xssfReader.getStylesTable();
                XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
                int index = 0;
                while (iter.hasNext()) {
                    InputStream stream = iter.next();
                    String sheetName = iter.getSheetName();
                    processSheet(styles, stringsTable, new SheetToCSV(), stream);
                    stream.close();
                    index++;
                }
            }
        }
    }

    public void processSheet(StylesTable styles, ReadOnlySharedStringsTable stringsTable, XSSFSheetXMLHandler.SheetContentsHandler sheetHandler, InputStream sheetInputStream)
            throws IOException, SAXException
    {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, stringsTable, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }


    private static boolean validateFileTypeFromSuffix(final File file,String suffix){
        String fileSuffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if(suffix.equalsIgnoreCase(fileSuffix)){
            return true;
        }
        return false;
    }

    public static void readContent(final File file) throws Exception{
//        Workbook workbook = try2HandleHSSF(file);
//        if(workbook==null){
//            workbook=try2HandleXSSF(file);
//        }
        Workbook workbook = WorkbookFactory.create(file);
        if(workbook!=null){
//        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
            int sheetNum = workbook.getNumberOfSheets();
            int rowOffset=0,columnOffset=1,firstRowNum,lastRowNum,lastColumnNum;
            StringBuffer rowContent=new StringBuffer("");
            for(int i = 0; i < sheetNum; i++){
                Sheet sheet = workbook.getSheetAt(i);
                firstRowNum = sheet.getFirstRowNum();
                lastRowNum = sheet.getLastRowNum();

                Cell cell;Row row;NumberFormat nf = NumberFormat.getInstance();
                for(int j=rowOffset;j<=lastRowNum;j++){
                    row = sheet.getRow(j);
                    lastColumnNum = row.getLastCellNum();
                    rowContent.delete(0,rowContent.length());
                    for(int k=0;k<=lastColumnNum;k++){
                        cell = row.getCell(k);
                        if(cell!=null){
                            switch (cell.getCellTypeEnum()){
                                case STRING:
                                    rowContent.append(cell.getStringCellValue()+",");
                                    break;
                                case NUMERIC:
                                    rowContent.append(nf.format(cell.getNumericCellValue()).replace(",", "")+",");
                                    break;
                                case BOOLEAN:
                                    rowContent.append(cell.getBooleanCellValue()+",");
                                    break;
                                case BLANK:
                                    rowContent.append(",");
                                    break;
                                default:
                                    rowContent.append(cell.toString()+",");
                                    break;

                            }
                        }
                    }
                    appendNewLine(file.getName(),rowContent.toString());
                }
            }
        }
    }
    private static Workbook try2HandleHSSF(File file){
        try{
            return new HSSFWorkbook(new FileInputStream(file));
        }catch (Exception e){
            return null;
        }
    }

    private static Workbook try2HandleXSSF(File file){
        Workbook workbook;
        try{
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            try {
//                if (POIFSFileSystem.hasPOIFSHeader(in)) {
//                    // if the file is encrypted
//                    POIFSFileSystem fs = new POIFSFileSystem(in);
//                    EncryptionInfo info = new EncryptionInfo(fs);
//                    Decryptor d = Decryptor.getInstance(info);
//                    d.verifyPassword(Decryptor.DEFAULT_PASSWORD);
//                    workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(d.getDataStream(fs));
//                }
//                else
//                    return new org.apache.poi.xssf.usermodel.XSSFWorkbook(in);
            }
            finally {
                in.close();
            }
            return new HSSFWorkbook(new FileInputStream(file));
        }catch (Exception e){
            return null;
        }
    }

    public static void parseExcel2007(final File file) throws Exception{

//        FileInputStream fis = new FileInputStream(file);
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fis);
//        int sheetNum = hssfWorkbook.getNumberOfSheets();
//        int rowOffset=0,columnOffset=1,firstRowNum,lastRowNum,lastColumnNum;
//        StringBuffer rowContent=new StringBuffer("");
//        for(int i = 0; i < sheetNum; i++){
//            HSSFSheet sheet = hssfWorkbook.getSheetAt(i);
//            firstRowNum = sheet.getFirstRowNum();
//            lastRowNum = sheet.getLastRowNum();
//
//            HSSFCell hssfCell;HSSFRow hssfRow;NumberFormat nf = NumberFormat.getInstance();
//            for(int j=rowOffset;j<=lastRowNum;j++){
//                hssfRow = sheet.getRow(j);
//                lastColumnNum = hssfRow.getLastCellNum();
//                rowContent.delete(0,rowContent.length());
//                for(int k=0;k<=lastColumnNum;k++){
//                    hssfCell = hssfRow.getCell(k);
//                    if(hssfCell!=null){
//                        switch (hssfCell.getCellType()){
//                            case HSSFCell.CELL_TYPE_STRING:
//                                rowContent.append(hssfCell.getStringCellValue()+",");
//                                break;
//                            case HSSFCell.CELL_TYPE_NUMERIC:
//                                rowContent.append(nf.format(hssfCell.getNumericCellValue()).replace(",", "")+",");
//                                break;
//                            default:
//                                break;
//
//                        }
//                    }
//                }
//                appendNewLine(file.getName(),rowContent.toString());
//            }
//        }
    }

    private static void appendNewLine(String fileName,String content){
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

    public static void main(String[] ar) throws Exception{
        long beginTime=System.currentTimeMillis();
        OPCPackage pkg = OPCPackage.open(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"), PackageAccess.READ);
        FileParser fileParser=new FileParser(pkg,64);
//        FileParser.parseExcel(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\1月\\开发区分局1月份社会信息\\开发区海棠里小区业主信息.xls"));
//        FileParser.handleUpload(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"));
//        logger.info(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx").length()+".");
        fileParser.parseFileAsStream(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"));
        logger.info("parse file elasped time:"+(System.currentTimeMillis()-beginTime)+"ms.");
    }

    private class SheetToCSV
            implements XSSFSheetXMLHandler.SheetContentsHandler
    {
        private boolean firstCellOfRow = false;
        private int currentRow = -1;
        private int currentCol = -1;

        private SheetToCSV() {  }
        private void outputMissingRows(int number) { for (int i = 0; i < number; i++) {
            for (int j = 0; j < FileParser.this.minColumns; j++) {
//                XLSX2CSV.this.output.append(',');
            }
//            XLSX2CSV.this.output.append('\n');
        }
        }

        public void startRow(int rowNum)
        {
            outputMissingRows(rowNum - this.currentRow - 1);

            this.firstCellOfRow = true;
            this.currentRow = rowNum;
            this.currentCol = -1;
        }

        public void endRow(int rowNum)
        {
            for (int i = this.currentCol; i < FileParser.this.minColumns; i++) {
//                XLSX2CSV.this.output.append(',');
            }
//            XLSX2CSV.this.output.append('\n');
        }

        public void cell(String cellReference, String formattedValue, XSSFComment comment)
        {
            if (this.firstCellOfRow)
                this.firstCellOfRow = false;
            else {
//                XLSX2CSV.this.output.append(',');
            }

            if (cellReference == null) {
                cellReference = new CellAddress(this.currentRow, this.currentCol).formatAsString();
            }

            int thisCol = new CellReference(cellReference).getCol();
            int missedCols = thisCol - this.currentCol - 1;
            for (int i = 0; i < missedCols; i++) {
//                XLSX2CSV.this.output.append(',');
            }
            this.currentCol = thisCol;
            try
            {
                Double.parseDouble(formattedValue);
//                XLSX2CSV.this.output.append(formattedValue);
            } catch (NumberFormatException e) {
//                XLSX2CSV.this.output.append('"');
//                XLSX2CSV.this.output.append(formattedValue);
//                XLSX2CSV.this.output.append('"');
            }
        }

        public void headerFooter(String text, boolean isHeader, String tagName)
        {
        }
    }

}
