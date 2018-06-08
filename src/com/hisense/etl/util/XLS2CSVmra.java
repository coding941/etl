package com.hisense.etl.util;

import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class XLS2CSVmra
        implements HSSFListener
{
    private int minColumns;
    private POIFSFileSystem fs;
    private PrintStream output;
    private int lastRowNumber;
    private int lastColumnNumber;
    private boolean outputFormulaValues = true;
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private List<BoundSheetRecord> boundSheetRecords = new ArrayList();
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;

    public XLS2CSVmra(POIFSFileSystem fs, PrintStream output, int minColumns)
    {
        this.fs = fs;
        this.output = output;
        this.minColumns = minColumns;
    }

    public XLS2CSVmra(String filename, int minColumns)
            throws IOException, FileNotFoundException
    {
        this(new POIFSFileSystem(new FileInputStream(filename)), System.out, minColumns);
    }

    public void process()
            throws IOException
    {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        this.formatListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (this.outputFormulaValues) {
            request.addListenerForAllRecords(this.formatListener);
        } else {
            this.workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(this.formatListener);
            request.addListenerForAllRecords(this.workbookBuildingListener);
        }

        factory.processWorkbookEvents(request, this.fs);
    }

    public void processRecord(Record record)
    {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;

        switch (record.getSid())
        {
            case 133:
                this.boundSheetRecords.add((BoundSheetRecord)record);
                break;
            case 2057:
                BOFRecord br = (BOFRecord)record;
                if (br.getType() == 16)
                {
                    if ((this.workbookBuildingListener != null) && (this.stubWorkbook == null)) {
                        this.stubWorkbook = this.workbookBuildingListener.getStubHSSFWorkbook();
                    }

                    this.sheetIndex += 1;
                    if (this.orderedBSRs == null) {
                        this.orderedBSRs = BoundSheetRecord.orderByBofPosition(this.boundSheetRecords);
                    }
                    this.output.println();
                    this.output.println(this.orderedBSRs[this.sheetIndex].getSheetname() + " [" + (this.sheetIndex + 1) + "]:"); } break;
            case 252:
                this.sstRecord = ((SSTRecord)record);
                break;
            case 513:
                BlankRecord brec = (BlankRecord)record;

                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
                thisStr = "";
                break;
            case 517:
                BoolErrRecord berec = (BoolErrRecord)record;

                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
                thisStr = "";
                break;
            case 6:
                FormulaRecord frec = (FormulaRecord)record;

                thisRow = frec.getRow();
                thisColumn = frec.getColumn();

                if (this.outputFormulaValues) {
                    if (Double.isNaN(frec.getValue()))
                    {
                        this.outputNextStringRecord = true;
                        this.nextRow = frec.getRow();
                        this.nextColumn = frec.getColumn();
                    } else {
                        thisStr = this.formatListener.formatNumberDateCell(frec);
                    }
                }
                else thisStr = '"' + HSSFFormulaParser.toFormulaString(this.stubWorkbook, frec.getParsedExpression()) + '"';

                break;
            case 519:
                if (this.outputNextStringRecord)
                {
                    StringRecord srec = (StringRecord)record;
                    thisStr = srec.getString();
                    thisRow = this.nextRow;
                    thisColumn = this.nextColumn;
                    this.outputNextStringRecord = false;
                }break;
            case 516:
                LabelRecord lrec = (LabelRecord)record;

                thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
                thisStr = '"' + lrec.getValue() + '"';
                break;
            case 253:
                LabelSSTRecord lsrec = (LabelSSTRecord)record;

                thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (this.sstRecord == null)
                    thisStr = "\"(No SST Record, can't identify string)\"";
                else {
                    thisStr = '"' + this.sstRecord.getString(lsrec.getSSTIndex()).toString() + '"';
                }
                break;
            case 28:
                NoteRecord nrec = (NoteRecord)record;

                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();

                thisStr = "\"(TODO)\"";
                break;
            case 515:
                NumberRecord numrec = (NumberRecord)record;

                thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();

                thisStr = this.formatListener.formatNumberDateCell(numrec);
                break;
            case 638:
                RKRecord rkrec = (RKRecord)record;

                thisRow = rkrec.getRow();
                thisColumn = rkrec.getColumn();
                thisStr = "\"(TODO)\"";
                break;
        }

        if ((thisRow != -1) && (thisRow != this.lastRowNumber)) {
            this.lastColumnNumber = -1;
        }

        if ((record instanceof MissingCellDummyRecord)) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = "";
        }

        if (thisStr != null) {
            if (thisColumn > 0) {
                this.output.print(',');
            }
            this.output.print(thisStr);
        }

        if (thisRow > -1)
            this.lastRowNumber = thisRow;
        if (thisColumn > -1) {
            this.lastColumnNumber = thisColumn;
        }

        if ((record instanceof LastCellOfRowDummyRecord))
        {
            if (this.minColumns > 0)
            {
                if (this.lastColumnNumber == -1) this.lastColumnNumber = 0;
                for (int i = this.lastColumnNumber; i < this.minColumns; i++) {
                    this.output.print(',');
                }

            }

            this.lastColumnNumber = -1;

            this.output.println();
        }
    }

    public static void main(String[] args) throws Exception {
//        if (args.length < 1) {
//            System.err.println("Use:");
//            System.err.println("  XLS2CSVmra <xls file> [min columns]");
//            System.exit(1);
//        }
//
//        int minColumns = -1;
//        if (args.length >= 2) {
//            minColumns = Integer.parseInt(args[1]);
//        }
        int minColumns=100;
        XLS2CSVmra xls2csv = new XLS2CSVmra("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\自来水信息\\水卡档案（西区）7.20上报\\水卡档案（海王所）.xls", minColumns);
        xls2csv.process();
    }
}
