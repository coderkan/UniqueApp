package com.eg.uniqueapp.main;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.eg.uniqueapp.R;
import com.eg.uniqueapp.control.PhoneChecker;
import com.eg.uniqueapp.model.DeviceInfoExt;
import com.eg.uniqueapp.model.Model;
import com.eg.uniqueapp.network.NetworkChecker;
import com.eg.uniqueapp.network.Type;
import com.eg.uniqueapp.shared.SharedUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.jaredrummler.android.device.DeviceName;

import java.util.ArrayList;

/**
 * Created by Erkan.Guzeler on 31.01.2017.
 */

public class MainPresenter {

    private Context context = null;
    private MainView view = null;
    private Model refModel = null;

    public MainPresenter(Context context, MainView view){
        this.context = context;
        this.view = view;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MainView getView() {
        return view;
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public void loadDeviceInfo() {
        DeviceName.with(this.context).request(new DeviceName.Callback() {

            @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String name = info.marketName;            // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"
                String versionRelease = Build.VERSION.RELEASE;
                DeviceInfoExt.Instance().add(info);
                Log.e("TAG","Manufacturer : " + manufacturer + " : " + " Name : " + name + " Model : " + model + " Codename : " + codename + " DeviceName : " + deviceName + " VersionRelease : " + versionRelease );
            }
        });
    }
    public void loadRef() {
        Model referenceModel = new Model("-1", PhoneChecker.getInstance().getDeviceId(), PhoneChecker.getInstance().getAndroidId(),DeviceInfoExt.Instance());
        this.refModel = referenceModel;
        this.view.onLoadModel(referenceModel);
    }


    public void controlInit() {
        boolean hasAppId = false;
        boolean hasNetworkAvailable = true;

        // Network Control
        if(NetworkChecker.isNetWorkAvailable(this.context) == Type.NONE)
            hasNetworkAvailable = false;

        String appId = SharedUtil.getValue(this.context, this.context.getString(R.string.preference_app_id));
        if (!appId.equals(SharedUtil.defValue))
            hasAppId = true;

        if(appId.equals("-1")){

            if(!hasNetworkAvailable){
                this.view.onShowMessageDialog("Cihaz Kullanılamaz Lütfen İnternet Bağlantısını Kontrol Ediniz...");
                return;
            }else{
                this.view.onLoadAuth();
                return;
            }
        }else{
            if(!hasAppId && !hasNetworkAvailable){
                this.view.onShowMessageDialog("Lütfen Cihazınızı İnternete Bağlayıp tekrar deneyiniz");
                return;
            }
            this.view.onLoadAuth();
        }
    }

    public void getUpdates(DataSnapshot ds) {
        ArrayList<Model> list = new ArrayList<>();
        for(DataSnapshot data : ds.getChildren())
        {
            DatabaseReference ref = data.getRef();

            Model model = new Model();
            model.setApplicationId(data.getValue(Model.class).getApplicationId());
            model.setImeiId(data.getValue(Model.class).getImeiId());
            model.setAndroidId(data.getValue(Model.class).getAndroidId());
            model.setMarketName(data.getValue(Model.class).getMarketName());
            model.setManufacturer(data.getValue(Model.class).getManufacturer());
            model.setReleaseVersion(data.getValue(Model.class).getReleaseVersion());
            model.setCodename(data.getValue(Model.class).getCodename());
            model.setDate(data.getValue(Model.class).getDate());
            model.setRefKey(ref.getKey());
            list.add(model);
            Log.e("TAG", "Model Ref: " + ref.getKey().toString() + " : " + model.toString());
        }
        this.view.onLoadUpdates(list);
    }

}
