package com.eg.uniqueapp.model;

public class Model {

    private String mApplicationId;
    private String mImeiId;
    private String mAndroidId;

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

}
