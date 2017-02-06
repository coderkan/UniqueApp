package com.eg.uniqueapp.auth;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.eg.uniqueapp.control.PhoneChecker;
import com.eg.uniqueapp.model.DeviceInfoExt;
import com.eg.uniqueapp.model.Model;
import com.eg.uniqueapp.network.NetworkChecker;
import com.eg.uniqueapp.network.Type;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.jaredrummler.android.device.DeviceName;

import java.util.ArrayList;

/**
 * Created by Erkan.Guzeler on 31.01.2017.
 */

public class SettingsPresenter {

    private Context context = null;
    private SettingsView view = null;
    private Model refModel = null;

    public SettingsPresenter() {
    }

    public SettingsPresenter(Context context, SettingsView view) {
        this.context = context;
        this.view = view;
    }

    public void init(){
        Type network = Type.NONE;
        if(((network = NetworkChecker.isNetWorkAvailable(this.context)) == Type.NONE))
            this.view.onShowMessage("Check Your Network Connection...");
        if(network != Type.NONE)
            this.view.onOpenAuth();
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

    private void Log(String message){
        Log.e("TAG",message);
    }

    public void auth(String email, String ath, ArrayList<Model> list) {

        boolean em = true;
        boolean at = true;
        if(email.length() == 0)
            em = false;
        if(ath.length() == 0)
            at = false;

        if(NetworkChecker.isNetWorkAvailable(this.context) == Type.NONE){
            this.view.onShowMessage("Lütfen İnternet Bağlantınızı Kontrol Ediniz...");
            return;
        }

        if(!em && at){
            this.view.onShowMessage("Lütfen Email Alanını Boş Bırakmayınız...");
            return;
        }
        if(!at && em){
            this.view.onShowMessage("Lütfen Ürün Kodunu Boş Bırakmayınız...");
            return;
        }
        if(!em && !at){
            this.view.onShowMessage("Lütfen Email ve Ürün Kodunu Boş Bırakmayınız...");
            return;
        }

        boolean isEmptyAndroidId = false;
        boolean isEmptyImeiId = false;
        boolean isFoundedApplicationId = false;
        if(list.size() != 0){
            for(int i = 0; i < list.size(); i++) {

                Model lmodel = list.get(i);

                if(lmodel.getApplicationId().equals(ath)){
                    this.refModel.setApplicationId(ath);
                    isFoundedApplicationId = true;
                    if(lmodel.getAndroidId().length() != 0){
                        if(lmodel.getAndroidId().equals(this.refModel.getAndroidId())){
                            isEmptyAndroidId = false;
                            this.view.onShowMessage("Veriler Daha Önceden Girilmiştir...");
                        }else{
                        }
                    }else{
                        isEmptyAndroidId = true;
                    }

                    if(lmodel.getImeiId().length() != 0){
                        if(lmodel.getImeiId().equals(this.refModel.getImeiId())){
                            isEmptyImeiId = false;
                            this.view.onShowMessage("Veriler Daha Önceden Girilmiştir...");
                        }else{
                        }
                    }else{
                        isEmptyImeiId = true;
                    }
                    if(isEmptyAndroidId || isEmptyImeiId ){
                        if(lmodel.getRefKey().length() != 0){
                            this.refModel.generateDate();
                            this.refModel.setmEmail(email);
                            this.view.onLoadFirebase(lmodel,this.refModel);
                            this.view.onShowMessage("Başarılı bir şekilde veriler kaydedilmiştir...");
                        }
                    }
                }else{
                }
            }
            if(!isFoundedApplicationId)
                this.view.onShowMessage("Not Found Application Id");
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
