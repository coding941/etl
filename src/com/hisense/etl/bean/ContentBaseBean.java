package com.hisense.etl.bean;


import java.util.HashMap;
import java.util.Map;

public class ContentBaseBean {

    protected String organizationCode;//组织机构编码
    protected String organizationName;//组织机构名称,全称/简称
    protected String fileName;
    protected String fileAbsolutePath;//文件全路径
    protected String recorderCode;//记录员编码或姓名
    protected String recordDate;//记录日期(年月日 时分秒)
    protected String excelType;//数据所属类型,数据字典：data_type_qb
    protected String recordUploadDate;//记录产生的日期（年/年月/年月日）
    protected String uploadOrg;//采集单位,自定义值集
    protected String reviserCode;//修改人编码/姓名
    protected String reviseDate;//修改日期(年月日 时分秒)
    protected String dataItems;//数据项
    protected String dataArea;//数据区域

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }


    public String getRecorderCode() {
        return recorderCode;
    }

    public void setRecorderCode(String recorderCode) {
        this.recorderCode = recorderCode;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getExcelType() {
        return excelType;
    }

    public void setExcelType(String excelType) {
        this.excelType = excelType;
    }

    public String getRecordUploadDate() {
        return recordUploadDate;
    }

    public void setRecordUploadDate(String recordUploadDate) {
        this.recordUploadDate = recordUploadDate;
    }

    public String getUploadOrg() {
        return uploadOrg;
    }

    public void setUploadOrg(String uploadOrg) {
        this.uploadOrg = uploadOrg;
    }

    public String getReviserCode() {
        return reviserCode;
    }

    public void setReviserCode(String reviserCode) {
        this.reviserCode = reviserCode;
    }

    public String getReviseDate() {
        return reviseDate;
    }

    public void setReviseDate(String reviseDate) {
        this.reviseDate = reviseDate;
    }

    public String getDataItems() {
        return dataItems;
    }

    public void setDataItems(String dataItems) {
        this.dataItems = dataItems;
    }

    public String getDataArea() {
        return dataArea;
    }

    public void setDataArea(String dataArea) {
        this.dataArea = dataArea;
    }


    public StudentCollegeContMetaData fromMap(Map<String,Object> own){
        StudentCollegeContMetaData bean=new StudentCollegeContMetaData();
        bean.setFileName(String.valueOf(own.get("fileName")));
        bean.setFileAbsolutePath(String.valueOf(own.get("fileAbsolutePath")));
        bean.setOrganizationName(String.valueOf(own.get("organizationName")));
        bean.setOrganizationCode(String.valueOf(own.get("organizationCode")));
        bean.setRecorderCode(String.valueOf(own.get("recorderCode")));
        bean.setRecordDate(String.valueOf(own.get("recordDate")));

        bean.setExcelType(String.valueOf(own.get("excelType")));
        bean.setRecordUploadDate(String.valueOf(own.get("recordUploadDate")));
        bean.setUploadOrg(String.valueOf(own.get("uploadOrg")));
        bean.setReviserCode(String.valueOf(own.get("reviserCode")));
        bean.setReviseDate(String.valueOf(own.get("reviseDate")));
        bean.setDataItems(String.valueOf(own.get("dataItems")));
        bean.setDataArea(String.valueOf(own.get("dataArea")));

        return bean;
    }
    public Map<String,Object> toMap(final StudentCollegeContMetaData owner){
        Map<String,Object> map=new HashMap<String,Object>(32);
        map.put("fileName",owner.getFileName());
        map.put("fileAbsolutePath",owner.getFileAbsolutePath());
        map.put("orgName",owner.getOrganizationName());
        map.put("orgCode",owner.getOrganizationCode());
        map.put("recorderCode",owner.getRecorderCode());
        map.put("recordDate",owner.getRecordDate());
        map.put("excelType",owner.getExcelType());
        map.put("recordUploadDate",owner.getRecordUploadDate());
        map.put("uploadOrg",owner.getUploadOrg());
        map.put("reviserCode",owner.getReviserCode());
        map.put("reviseDate",owner.getReviseDate());
        map.put("dataItems",owner.getDataItems());
        map.put("dataArea",owner.getDataArea());
        return map;
    }
}
