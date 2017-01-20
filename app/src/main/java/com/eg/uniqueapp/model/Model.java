package com.eg.uniqueapp.model;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Model {

    private String mApplicationId;
    private String mImeiId;
    private String mAndroidId;
    private String mDate;

    //
    private String mManufacturer;
    private String mMarketName;
    private String mCodename;
    private String mModel;
    private String mReleaseVersion = Build.VERSION.RELEASE;;

    public Model(){}
    public Model(String mApplicationId, String mImeiId, String mAndroidId){
        this.mApplicationId = mApplicationId;
        this.mImeiId = mImeiId;
        this.mAndroidId = mAndroidId;
    }
    public String getApplicationId() {
        return mApplicationId;
    }

    public void setApplicationId(String mApplicationId) {
        this.mApplicationId = mApplicationId;
    }

    public String getImeiId() {
        return mImeiId;
    }

    public void setImeiId(String mImeiId) {
        this.mImeiId = mImeiId;
    }

    public String getAndroidId() {
        return mAndroidId;
    }

    public void setAndroidId(String mAndroidId) {
        this.mAndroidId = mAndroidId;
    }

    public String toString(){
        return new String(" ApplicationId : " + this.mApplicationId + " , IMEIID : " + this.mImeiId);
    }

    public void add(DeviceInfoExt ext){
        setCodename(ext.getCodename());
        setManufacturer(ext.getManufacturer());
        setMarketName(ext.getMarketName());
        setModel(ext.getModel());
        setReleaseVersion(ext.getReleaseVersion());
    }



    public void generateDate(){
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.mDate = databaseDateTimeFormate.format(new Date());     //2009-06-30 08:29:
    }

    public String getDate(){
        return this.mDate;
    }

    public void setDate(String date){
        this.mDate = date;
    }

    public String getCodename() {
        return mCodename;
    }

    public void setCodename(String codename) {
        this.mCodename = codename;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public String getReleaseVersion() {
        return mReleaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.mReleaseVersion = releaseVersion;
    }

    public String getMarketName() {
        return mMarketName;
    }

    public void setMarketName(String marketName) {
        this.mMarketName = marketName;
    }

    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.mManufacturer = manufacturer;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
}
