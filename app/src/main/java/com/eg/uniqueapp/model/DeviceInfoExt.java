package com.eg.uniqueapp.model;


import android.os.Build;

import com.jaredrummler.android.device.DeviceName;

/**
 * Created by Erkan.Guzeler on 20.01.2017.
 */

public class DeviceInfoExt {
    private String manufacturer;
    private String marketName;
    private String codename;
    private String model;
    private String releaseVersion = Build.VERSION.RELEASE;;

    private static DeviceInfoExt instance = null;

    public static DeviceInfoExt Instance(){
        if(instance == null)
            instance = new DeviceInfoExt();
        return instance;
    }

    public DeviceInfoExt(){}
    public void add(String manufacturer, String marketName, String codename, String model,String releaseVersion) {
        this.manufacturer = manufacturer;
        this.marketName = marketName;
        this.codename = codename;
        this.model = model;
        this.releaseVersion = releaseVersion;
    }

    public void add(DeviceName.DeviceInfo info){
        this.manufacturer = info.manufacturer;
        this.marketName = info.marketName;
        this.codename = info.codename;
        this.model = info.model;
    }
    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getReleaseVersion() {
        return releaseVersion = Build.VERSION.RELEASE;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
