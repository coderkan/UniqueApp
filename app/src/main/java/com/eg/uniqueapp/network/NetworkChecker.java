package com.eg.uniqueapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * Created by erkan on 03.12.2016.
 */
public class NetworkChecker {
    private static String TAG = "NetworkChecker";
    public static Type isNetWorkAvailable(Context context){
        Type val = Type.NONE;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null){
            Log.e(TAG,"Connectivity Manager is NULL");
            return Type.NONE;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null){
            Log.e(TAG,"networkInfo Manager is NULL");
            return Type.NONE;
        }
        if(networkInfo.isAvailable() || networkInfo.isConnected() || networkInfo.isConnectedOrConnecting()){
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                val = Type.WIFI;
            }if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                val = Type.MOBILE;
            }
        }else
            return Type.NONE;
        return val;
    }
}