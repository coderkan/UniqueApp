package com.eg.uniqueapp.control;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Erkan.Guzeler on 19.01.2017.
 */
public class PhoneChecker {
    private String deviceId;
    private String androidId;

    private static PhoneChecker ourInstance = null;

    public static PhoneChecker getInstance() {
        if(ourInstance == null)
            ourInstance = new PhoneChecker();
        return ourInstance;
    }

    private PhoneChecker() {
    }
    public void initialize(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.deviceId = telephonyManager.getDeviceId();
        this.androidId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getDeviceId(){
        return this.deviceId;
    }

    public String getAndroidId(){
        return this.androidId;
    }

}
