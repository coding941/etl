package com.hisense.etl.util.pc.bean;

public class AlarmInfo {
    private String id;
    private AlarmType type;
    private String extraInfo;

    public AlarmInfo(){

    }

    public AlarmInfo(String id,AlarmType type){
        this.id=id;
        this.type=type;
    }

    public void setExtraInfo(String extraInfo){
        this.extraInfo=extraInfo;
    }

}
