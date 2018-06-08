package com.hisense.etl.util;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileOperatorUtil {
    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(Class.class);

    public void parseHssfAsStream(String fullPathFileName) throws InvalidFormatException,OpenXML4JException,IOException,SAXException{
        parseHssfAsStream(new File(fullPathFileName));
    }

    public void parseHssfAsStream(File file) throws InvalidFormatException,OpenXML4JException,IOException,SAXException{
        OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ);
        try {
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();

            XMLReader parser = fetchSheetParser(sst);

            InputStream sheet2 = (InputStream)r.getSheetsData().next();
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            sheet2.close();
        } finally {
            pkg.close();
        }
    }

    public void parseXssfAsStream(String fullPathFileName) throws InvalidFormatException,OpenXML4JException,IOException,SAXException{
        parseXssfAsStream(new File(fullPathFileName));
    }

    public void parseXssfAsStream(File file) throws InvalidFormatException,OpenXML4JException,IOException,SAXException{
        if(!file.exists())return;
        OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ);
        try {
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            InputStream sheet2 = (InputStream)r.getSheetsData().next();
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            sheet2.close();
        } finally {
            pkg.close();
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

    public void parseXssfAllSheetsAsStream(File file) throws Exception {
        OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ);
        try {
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();

            XMLReader parser = fetchSheetParser(sst);

            Iterator sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                System.out.println("Processing new sheet:\n");
                InputStream sheet = (InputStream)sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                sheet.close();
                System.out.println("");
            }
        } finally {
            pkg.close();
        }
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    public static void main(String[] ar) throws Exception{
        long beginTime=System.currentTimeMillis();
        FileOperatorUtil operatorUtil = new FileOperatorUtil();
        operatorUtil.parseXssfAsStream(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"));
//        operatorUtil.parseXssfAllSheetsAsStream(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\黄岛区自来水水卡档案信息（新华所、海王所）.xlsx"));
        logger.info("parse file elasped time:"+(System.currentTimeMillis()-beginTime)+"ms.");
    }

    private static class SheetHandler extends DefaultHandler
    {
        private final SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private boolean inlineStr;
        private final LruCache<Integer, String> lruCache = new LruCache(64);

        private SheetHandler(SharedStringsTable sst)
        {
            this.sst = sst;
        }

        public void startElement(String uri, String localName, String name, Attributes attributes)
                throws SAXException
        {
            if (name.equals("c"))
            {
                System.out.print(attributes.getValue("r") + " - ");

                String cellType = attributes.getValue("t");
                this.nextIsString = ((cellType != null) && (cellType.equals("s")));
                this.inlineStr = ((cellType != null) && (cellType.equals("inlineStr")));
            }

            this.lastContents = "";

        }

        public void endElement(String uri, String localName, String name)
                throws SAXException
        {
            if (this.nextIsString) {
                Integer idx = Integer.valueOf(this.lastContents);
                this.lastContents = ((String)this.lruCache.get(idx));
                if ((this.lastContents == null) && (!this.lruCache.containsKey(idx))) {
                    this.lastContents = new XSSFRichTextString(this.sst.getEntryAt(idx.intValue())).toString();
                    this.lruCache.put(idx, this.lastContents);
                }
                this.nextIsString = false;
            }

            if ((name.equals("v")) || ((this.inlineStr) && (name.equals("c"))))
                System.out.println(this.lastContents);
        }

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

        public void characters(char[] ch, int start, int length)
                throws SAXException
        {
            this.lastContents += new String(ch, start, length);
        }

        private static class LruCache<A, B> extends LinkedHashMap<A, B>
        {
            private final int maxEntries;

            public LruCache(int maxEntries)
            {
                super(maxEntries,1.0F, true);
                this.maxEntries = maxEntries;
            }

            protected boolean removeEldestEntry(Map.Entry<A, B> eldest)
            {
                return super.size() > this.maxEntries;
            }
        }
    }
}
