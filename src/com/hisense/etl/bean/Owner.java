package com.hisense.etl.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Owner {
    private String unitHouseNum;
    private Date collectTime;
    private String propertyPerson;
    private String ownerPhone;
    private String unitName;
    private String subCouncil;
    private String unitPhone;
    private String policeStation;
    private String communityAddress;
    private boolean isClosed;
    private String ownerName;
    private String propertyName;
    private String unitPerson;
    private String ownerHouseNum;
    private String builtYear;
    private String communityName;
    private String propertyPhone;
    private String importTime;
    private String taskId;
    private String collectPoliceNumber;
    private String ownerIDCard;

    public Owner fromMap(Map<String,Object> own){
        Owner bean=new Owner();
        bean.setUnitHouseNum(String.valueOf(own.get("unitHouseNum")));
        bean.setCollectTime((Date)(own.get("collectTime")));
        bean.setPropertyPerson(String.valueOf(own.get("propertyPerson")));
        bean.setOwnerPhone(String.valueOf(own.get("ownerPhone")));
        bean.setUnitName(String.valueOf(own.get("unitName")));
        return bean;
    }
    public Map<String,Object> toMap(final Owner owner){
        Map<String,Object> map=new HashMap<String,Object>(32);
        map.put("unitHouseNum",owner.getUnitHouseNum());
        map.put("collectTime",owner.getCollectTime());
        map.put("propertyPerson",owner.getPropertyPerson());
        map.put("ownerPhone",owner.getOwnerPhone());
        map.put("unitName",owner.getUnitName());
        map.put("subCouncil",owner.getSubCouncil());
        map.put("unitPhone",owner.getUnitPhone());
        map.put("policeStation",owner.getPoliceStation());
        return map;
    }


    public String getUnitHouseNum() {
        return unitHouseNum;
    }

    public void setUnitHouseNum(String unitHouseNum) {
        this.unitHouseNum = unitHouseNum;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getPropertyPerson() {
        return propertyPerson;
    }

    public void setPropertyPerson(String propertyPerson) {
        this.propertyPerson = propertyPerson;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSubCouncil() {
        return subCouncil;
    }

    public void setSubCouncil(String subCouncil) {
        this.subCouncil = subCouncil;
    }

    public String getUnitPhone() {
        return unitPhone;
    }

    public void setUnitPhone(String unitPhone) {
        this.unitPhone = unitPhone;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getCommunityAddress() {
        return communityAddress;
    }

    public void setCommunityAddress(String communityAddress) {
        this.communityAddress = communityAddress;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getUnitPerson() {
        return unitPerson;
    }

    public void setUnitPerson(String unitPerson) {
        this.unitPerson = unitPerson;
    }

    public String getOwnerHouseNum() {
        return ownerHouseNum;
    }

    public void setOwnerHouseNum(String ownerHouseNum) {
        this.ownerHouseNum = ownerHouseNum;
    }

    public String getBuiltYear() {
        return builtYear;
    }

    public void setBuiltYear(String builtYear) {
        this.builtYear = builtYear;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getPropertyPhone() {
        return propertyPhone;
    }

    public void setPropertyPhone(String propertyPhone) {
        this.propertyPhone = propertyPhone;
    }

    public String getImportTime() {
        return importTime;
    }

    public void setImportTime(String importTime) {
        this.importTime = importTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCollectPoliceNumber() {
        return collectPoliceNumber;
    }

    public void setCollectPoliceNumber(String collectPoliceNumber) {
        this.collectPoliceNumber = collectPoliceNumber;
    }

    public String getOwnerIDCard() {
        return ownerIDCard;
    }

    public void setOwnerIDCard(String ownerIDCard) {
        this.ownerIDCard = ownerIDCard;
    }


}
