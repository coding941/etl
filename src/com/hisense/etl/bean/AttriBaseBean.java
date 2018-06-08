package com.hisense.etl.bean;


public class AttriBaseBean {

    public String getItemEngName() {
        return itemEngName;
    }

    public void setItemEngName(String itemEngName) {
        this.itemEngName = itemEngName;
    }

    public String getItemChiName() {
        return itemChiName;
    }

    public void setItemChiName(String itemChiName) {
        this.itemChiName = itemChiName;
    }

    public String getItemChiAbbr() {
        return itemChiAbbr;
    }

    public void setItemChiAbbr(String itemChiAbbr) {
        this.itemChiAbbr = itemChiAbbr;
    }

    public String getItemDataType() {
        return itemDataType;
    }

    public void setItemDataType(String itemDataType) {
        this.itemDataType = itemDataType;
    }

    public String getItemDataType4Db() {
        return itemDataType4Db;
    }

    public void setItemDataType4Db(String itemDataType4Db) {
        this.itemDataType4Db = itemDataType4Db;
    }

    protected String itemEngName;//数据项英文名称
    protected String itemChiName;//数据项中文名称
    protected String itemChiAbbr;//数据项中文简称
    protected String itemDataType;//ES数据类型:text/keyword
    protected String itemDataType4Db;//DB数据类型:varchar,date,int


}
