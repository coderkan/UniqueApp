package com.eg.uniqueapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eg.uniqueapp.auth.SettingsActivity;
import com.eg.uniqueapp.control.PhoneChecker;
import com.eg.uniqueapp.model.DeviceInfoExt;
import com.eg.uniqueapp.model.Model;
import com.eg.uniqueapp.network.NetworkChecker;
import com.eg.uniqueapp.network.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.android.device.DeviceName;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ChildEventListener {


    private final static int CAMERA_RESULT = 103;
    private final static int READ_EXTERNAL_RESULT = 104;
    private final static int WRITE_EXTERNAL_RESULT = 105;
    private final static int ACCESS_NETWORK_RESULT = 106;
    private final static int ACCESS_NETWORK_STATE_RESULT = 107;
    private final static int ALL_PERMISSIONS_RESULT = 108;

    ArrayList<String> permissions =  new ArrayList<String>();
    private boolean isPermissioned = false;
    private String deviceId = "";
    private String androidId = "";

    @BindView(R.id.text_views)
    TextView textView;


    private String strLog = "";

    ArrayList<Model> list = new ArrayList<>();

    private final static String TAG = "SettingsActivity";

    private FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener = null;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Type network = Type.NONE;
    private boolean isSignin = false;
    private String refKey = "";

    private Model referenceModel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();
        PhoneChecker.getInstance().initialize(this);
        deviceInfo();
        referenceModel = new Model();
        referenceModel.setApplicationId("-1");
        referenceModel.setAndroidId(PhoneChecker.getInstance().getAndroidId());
        referenceModel.setImeiId(PhoneChecker.getInstance().getDeviceId());
        referenceModel.add(DeviceInfoExt.Instance());

        textView.setText(PhoneChecker.getInstance().getDeviceId() + " :: " + PhoneChecker.getInstance().getAndroidId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String deviceName = DeviceName.getDeviceName();
//        Log.e("TAG","Device Name " + deviceName);
//        DeviceName.with(this).request(new DeviceName.Callback() {
//
//            @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
//                String manufacturer = info.manufacturer;  // "Samsung"
//                String name = info.marketName;            // "Galaxy S7 Edge"
//                String model = info.model;                // "SAMSUNG-SM-G935A"
//                String codename = info.codename;          // "hero2lte"
//                String deviceName = info.getName();       // "Galaxy S7 Edge"
//                // FYI: We are on the UI thread.
//                String versionRelease = Build.VERSION.RELEASE;
//
//                Log.e("TAG","Manufacturer : " + manufacturer + " : " + " Name : " + name + " Model : " + model + " Codename : " + codename + " DeviceName : " + deviceName + " VersionRelease : " + versionRelease );
//
//            }
//        });
        SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String d = databaseDateTimeFormate.format(new Date());     //2009-06-30 08:29:
        Log.e("TAG"," Date is : " + d);
        authControl();
    }
    private void deviceInfo() {
        DeviceName.with(this).request(new DeviceName.Callback() {

            @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String name = info.marketName;            // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"
                // FYI: We are on the UI thread.
                String versionRelease = Build.VERSION.RELEASE;
                DeviceInfoExt.Instance().add(info);
                Log.e("TAG","Manufacturer : " + manufacturer + " : " + " Name : " + name + " Model : " + model + " Codename : " + codename + " DeviceName : " + deviceName + " VersionRelease : " + versionRelease );
            }
        });
    }

    private void authControl() {
        if(NetworkChecker.isNetWorkAvailable(getApplicationContext()) == Type.NONE)
            return;
        this.list.clear();
        controlAuth();
    }

    @OnClick(R.id.fab) void fabButtonClick(View view){
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void controlAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.e(TAG,"onAuthStateChanged: signed_in: "+user.getUid());
                }else{
                    Log.e(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
        signIn();
        root.addChildEventListener(this);
    }
    private void signIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG,"signInAnonymously:onComplete: "+ task.isSuccessful());
                        if(!task.isSuccessful()){
                            Log.e(TAG,"signInAnonymously",task.getException());
                        }
                    }
                });
    }


    public ArrayList<String> getPermissionRequested(ArrayList<String> perms){
        ArrayList<String> permissions = new ArrayList<String>();
        for (String perm : perms
                ) {
            if(!hasPermission(perm))
                permissions.add(perm);
        }
        return permissions;
    }
    private boolean hasPermission(String permission){
        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    public void checkPermission(){
        permissions.clear();
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions = getPermissionRequested(permissions);
        if(permissions.size() == 0)
            isPermissioned = true;
        requestPermissions(permissions,ALL_PERMISSIONS_RESULT);
    }
    private void requestPermissions(ArrayList<String> permissions,int resultCode){
        if(permissions == null)
            return;
        if(permissions.size() > 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions.toArray(new String[permissions.size()]),resultCode);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        ArrayList<String> rejectedPermissions = new ArrayList<String>();
        switch (requestCode)
        {
            case ALL_PERMISSIONS_RESULT:
                for (String perm: permissions){
                    if(!hasPermission(perm)){
                        rejectedPermissions.add(perm);
                        Log.e("TAG","Rejected Permissions : " + perm);
                    }

                }
                if(rejectedPermissions.size() == 0){
                    Log.e("TAG", "All Permission is True");
                    isPermissioned = true;
                }
                requestPermissions(rejectedPermissions,ALL_PERMISSIONS_RESULT);
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
    }




    private void getUpdates(DataSnapshot ds) {
        list.clear();
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

            list.add(model);

            if(model.getApplicationId().equals("1")){
                Log.e("TAG","Model Application Id = 1");
                refKey = ref.getKey();
            }
            Log.e(TAG,"Model Ref: " + ref.getKey().toString() + " : " + model.toString());
        }

        //
        new TestAsync().execute();

    }

    private void addFireBase() {
        //Model model = new Model("1","11111111111", "2222222222222" );
        Model model = new Model("2",PhoneChecker.getInstance().getDeviceId(), PhoneChecker.getInstance().getAndroidId());
        model.add(DeviceInfoExt.Instance());
        model.generateDate();

        root.child("Users").push().setValue(model);
    }

    private void updateFireBase() {
        //Model model = new Model("1","11111111111", "2222222222222" );
        Model model = new Model("365",PhoneChecker.getInstance().getDeviceId(), PhoneChecker.getInstance().getAndroidId());
        model.add(DeviceInfoExt.Instance());
        model.generateDate();
        root.child("Users/"+refKey).setValue(model);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        getUpdates(dataSnapshot);
    }
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        getUpdates(dataSnapshot);
    }
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        getUpdates(dataSnapshot);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    class TestAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            for(int i = 0; i < list.size(); i++){
                compareList(list.get(i));
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);
        }
    }

    private void compareList(Model model) {
        if(referenceModel.getAndroidId() == model.getAndroidId()){
            Log.e(TAG, " compareList1" + model.getApplicationId());
        }
        if(referenceModel.getImeiId() == model.getImeiId()){
            Log.e(TAG, " compareList2" + model.getApplicationId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
