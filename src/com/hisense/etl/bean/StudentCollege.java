package com.hisense.etl.bean;

import java.util.HashMap;
import java.util.Map;

public class StudentCollege  {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeAddress() {
        return collegeAddress;
    }

    public void setCollegeAddress(String collegeAddress) {
        this.collegeAddress = collegeAddress;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getProfessionName() {
        return professionName;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
    }

    public String getExpectedGraduateDate() {
        return expectedGraduateDate;
    }

    public void setExpectedGraduateDate(String expectedGraduateDate) {
        this.expectedGraduateDate = expectedGraduateDate;
    }

    public String getAccommodationCampus() {
        return accommodationCampus;
    }

    public void setAccommodationCampus(String accommodationCampus) {
        this.accommodationCampus = accommodationCampus;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHousePhone() {
        return housePhone;
    }

    public void setHousePhone(String housePhone) {
        this.housePhone = housePhone;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public String getProfessionClass() {
        return professionClass;
    }

    public void setProfessionClass(String professionClass) {
        this.professionClass = professionClass;
    }

    public String getCultureLevel() {
        return cultureLevel;
    }

    public void setCultureLevel(String cultureLevel) {
        this.cultureLevel = cultureLevel;
    }

    public String getHouseDistrictName() {
        return houseDistrictName;
    }

    public void setHouseDistrictName(String houseDistrictName) {
        this.houseDistrictName = houseDistrictName;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public String getCollegeFormality() {
        return collegeFormality;
    }

    public void setCollegeFormality(String collegeFormality) {
        this.collegeFormality = collegeFormality;
    }

    public String getCollegeType() {
        return collegeType;
    }

    public void setCollegeType(String collegeType) {
        this.collegeType = collegeType;
    }

    public String getEnrollmentSeason() {
        return enrollmentSeason;
    }

    public void setEnrollmentSeason(String enrollmentSeason) {
        this.enrollmentSeason = enrollmentSeason;
    }

    private String name;//姓名
    private String usedName;//曾用名
    private String gender;//性别
    private String idcard;//身份证号
    private String studentId;//学号
    private String politicalStatus;//政治面貌
    private String healthStatus;//健康状况/身体状况
    private String birthday;//出生日期
    private String enrollmentDate;//入学日期
    private String collegeName;//学校名称
    private String collegeAddress;//学校所在地
    private String instituteName;//所在学院
    private String professionName;//专业名称
    private String expectedGraduateDate;//预计毕业日期
    private String accommodationCampus;//住宿校区（住址）
    private String grade;//班级
    private String housePhone;//家庭电话
    private String houseAddress;//家庭住址
    private String enrollmentStatus;//学籍状态
    private String professionClass;//专业类别
    private String cultureLevel;//培养层次
    private String houseDistrictName;//生源所在地区名称
    private String enrollmentType;//入学方式
    private String collegeFormality;//办学形式
    private String collegeType;//办学类型
    private String enrollmentSeason;//招生季节

    public StudentCollege fromMap(Map<String,Object> own){
        StudentCollege bean=new StudentCollege();
        bean.setName(String.valueOf(own.get("name")));
        bean.setGender(String.valueOf(own.get("gender")));
        bean.setIdcard(String.valueOf(own.get("idcard")));
        bean.setStudentId(String.valueOf(own.get("studentId")));
        bean.setPoliticalStatus(String.valueOf(own.get("politicalStatus")));

        bean.setHealthStatus(String.valueOf(own.get("healthStatus")));
        bean.setBirthday(String.valueOf(own.get("birthday")));
        bean.setEnrollmentDate(String.valueOf(own.get("enrollmentDate")));
        bean.setCollegeName(String.valueOf(own.get("collegeName")));
        bean.setCollegeAddress(String.valueOf(own.get("collegeAddress")));

        bean.setInstituteName(String.valueOf(own.get("instituteName")));
        bean.setProfessionName(String.valueOf(own.get("professionName")));
        bean.setExpectedGraduateDate(String.valueOf(own.get("expectedGraduateDate")));
        bean.setAccommodationCampus(String.valueOf(own.get("accommodationCampus")));
        bean.setGrade(String.valueOf(own.get("grade")));
        bean.setHousePhone(String.valueOf(own.get("housePhone")));
        bean.setHouseAddress(String.valueOf(own.get("houseAddress")));
        bean.setEnrollmentStatus(String.valueOf(own.get("enrollmentStatus")));
        bean.setProfessionClass(String.valueOf(own.get("professionClass")));
        bean.setCultureLevel(String.valueOf(own.get("cultureLevel")));
        bean.setHouseDistrictName(String.valueOf(own.get("houseDistrictName")));
        bean.setEnrollmentType(String.valueOf(own.get("enrollmentType")));
        bean.setCollegeFormality(String.valueOf(own.get("collegeFormality")));
        bean.setCollegeType(String.valueOf(own.get("collegeType")));
        bean.setEnrollmentSeason(String.valueOf(own.get("enrollmentSeason")));
        return bean;
    }
    public Map<String,Object> toMap(final StudentCollege owner){
        Map<String,Object> map=new HashMap<String,Object>(64);
        map.put("name",owner.getName());
        map.put("gender",owner.getGender());
        map.put("idcard",owner.getIdcard());
        map.put("studentId",owner.getStudentId());
        map.put("politicalStatus",owner.getPoliticalStatus());
        map.put("healthStatus",owner.getHealthStatus());
        map.put("birthday",owner.getBirthday());
        map.put("enrollmentDate",owner.getEnrollmentDate());

        map.put("collegeName",owner.getCollegeName());
        map.put("collegeAddress",owner.getCollegeAddress());
        map.put("instituteName",owner.getInstituteName());
        map.put("professionName",owner.getProfessionName());
        map.put("expectedGraduateDate",owner.getExpectedGraduateDate());
        map.put("accommodationCampus",owner.getAccommodationCampus());
        map.put("grade",owner.getGrade());
        map.put("housePhone",owner.getHousePhone());

        map.put("houseAddress",owner.getHouseAddress());
        map.put("enrollmentStatus",owner.getEnrollmentStatus());
        map.put("professionClass",owner.getProfessionClass());
        map.put("cultureLevel",owner.getCultureLevel());
        map.put("houseDistrictName",owner.getHouseDistrictName());
        map.put("enrollmentType",owner.getEnrollmentType());
        map.put("collegeFormality",owner.getCollegeFormality());
        map.put("collegeType",owner.getCollegeType());
        map.put("enrollmentSeason",owner.getEnrollmentSeason());
        return map;
    }





}
