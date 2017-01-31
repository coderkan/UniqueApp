package com.eg.uniqueapp.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.eg.uniqueapp.R;

/**
 * Created by Erkan.Guzeler on 25.01.2017.
 */

public class SharedUtil {
    public static String defValue = "#NONE#";
    public SharedUtil(){}
    public static void addValue(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static String getValue(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String val = sharedPreferences.getString(key,defValue);
        if(val.equals(defValue))
            return defValue;
        return val;
    }
}