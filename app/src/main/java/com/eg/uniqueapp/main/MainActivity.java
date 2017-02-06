package com.eg.uniqueapp.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.eg.uniqueapp.R;
import com.eg.uniqueapp.auth.SettingsActivity;
import com.eg.uniqueapp.control.PhoneChecker;
import com.eg.uniqueapp.model.Model;
import com.eg.uniqueapp.network.NetworkChecker;
import com.eg.uniqueapp.network.Type;
import com.eg.uniqueapp.shared.SharedUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ChildEventListener, MainView {

    private final static int ALL_PERMISSIONS_RESULT = 108;

    ArrayList<String> permissions =  new ArrayList<>();
    @BindView(R.id.text_views)
    TextView textView;


    ArrayList<Model> list = new ArrayList<>();

    private final static String TAG = "SettingsActivity";

    private FirebaseAuth mAuth = null;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private Model referenceModel = null;
    private boolean isRegistered = true;
    private boolean isPermissioned = false;
    private MainPresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission();
        PhoneChecker.getInstance().initialize(this);
        loadPresenter();
    }

    private void loadPresenter() {
        presenter = new MainPresenter(getApplicationContext(), this);
        presenter.loadDeviceInfo();
        presenter.loadRef();
        presenter.controlInit();
    }

    private void registerControl() {
        boolean hasAppId = false;
        String appId = SharedUtil.getValue(getApplicationContext(), getString(R.string.preference_app_id));
        if (!appId.equals(SharedUtil.defValue))
            hasAppId = true;

        if (list.size() != 0) {

            for (int i = 0; i < list.size(); i++) {

                Model lmodel = list.get(i);

                if (hasAppId) {
                    if (lmodel.getApplicationId().equals(appId)) { // App id kayıtlı diğer verilerin kontrolünü yap //
                        Log.e("TAG","App Idler eşleşmektedir...");
                        if(!lmodel.getApplicationId().equals("-1")){
                            isRegistered = true;
                            SharedUtil.addValue(getApplicationContext(), getString(R.string.preference_register), "1"); // Register oldu
                            Log.e("TAG","Register Oldu...");
                        }
                    } else {
                        Log.e("TAG","App Idler eşleşmemektedir...");

                        if (lmodel.getAndroidId().equals(referenceModel.getAndroidId())) {
                            Log.e("TAG", "Android Idler eşittir...");
                            String aid = lmodel.getApplicationId();
                            if(aid.length() != 0 && !aid.equals("-1")){
                                SharedUtil.addValue(getApplicationContext(),getString(R.string.preference_app_id),aid);
                                isRegistered = true;
                            }else
                                isRegistered = false;
                        } else {
                            //SharedUtil.addValue(getApplicationContext(), getString(R.string.preference_register), "2"); // Register olmadı
                            Log.e("TAG","XAndroid Idler eşit değildir..., Program Çalışmayacaktır...");
                        }

                        if(lmodel.getApplicationId().equals("-1")){
                            Log.e("TAG","App id  = -1 ");
                            isRegistered = false;
                        }

                    }
                } else {
                    if (lmodel.getAndroidId().equals(referenceModel.getAndroidId())) {
                        //SharedUtil.addValue(getApplicationContext(), getString(R.string.preference_register), "1"); // Register oldu
                        String aid = lmodel.getApplicationId();
                        if(aid.length() != 0 && !aid.equals("-1")){
                            SharedUtil.addValue(getApplicationContext(),getString(R.string.preference_app_id),aid);
                            isRegistered = true;
                        }else{
                            Log.e("TAG","Android Idler eşittir");
                            isRegistered = false;
                        }

                    } else {
                        //message("Android Idler eşit değildir...Uygulama Çalışmayacaktır...");
                        Log.e("TAG","Android Idler eşit değildir...Uygulama Çalışmayacaktır...");
                    }

                    if(lmodel.getApplicationId().equals("-1")){
                        Log.e("TAG","AppApp id  = -1 ");
                        isRegistered = false;
                    }
                }

            }

        }
    }



    private void authControl() {
        if(NetworkChecker.isNetWorkAvailable(getApplicationContext()) == Type.NONE)
            return;
        this.list.clear();
        controlAuth();
    }

    @OnClick(R.id.fab) void fabButtonClick(){
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void controlAuth() {
        mAuth = FirebaseAuth.getInstance();
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
        ArrayList<String> permissions = new ArrayList<>();
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
            Log.e("TAG","hasPermission " + permission);
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
        ArrayList<String> rejectedPermissions = new ArrayList<>();
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
                    Log.d("TAG", "All Permission is True");
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
        presenter.getUpdates(ds);
        new TestAsync().execute();
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

    @Override
    public void onLoadModel(Model referenceModel) {
        this.referenceModel = referenceModel;
    }

    @Override
    public void onShowMessageDialog(String msg) {
        //"Lütfen Cihazınızı İnternete Bağlayıp tekrar deneyiniz"
        DialogMessage dm = new DialogMessage(MainActivity.this, "Uyarı",msg,"Tamam");
        dm.build();
        dm.show();
    }

    @Override
    public void onLoadAuth() {
        authControl();
    }

    @Override
    public void onLoadUpdates(ArrayList<Model> list) {
        this.list.clear();
        this.list = list;
    }

    class TestAsync extends AsyncTask<Void, Integer, String>
    {
        AlertDialog.Builder alertDialogBuilder = null;
        String TAG = getClass().getSimpleName();
        protected void onPreExecute (){
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            isRegistered = false;
        }

        protected String doInBackground(Void...arg0) {
            registerControl();
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.e(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            if(!isRegistered){
                alertDialogBuilder.setTitle("Uyarı");
                alertDialogBuilder
                        .setMessage("Uygulama Bu Cihazda Kullanılamaz!")
                        .setCancelable(false)
                        .setPositiveButton("Tamam",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SharedUtil.addValue(getApplicationContext(), getString(R.string.preference_app_id),"-1");
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("Çıkış",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SharedUtil.addValue(getApplicationContext(), getString(R.string.preference_app_id),"-1");
                                dialog.cancel();
                                MainActivity.this.finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
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

    public class DialogMessage {
        private AlertDialog.Builder alertBuilder = null;
        private String title;
        private String message;
        private String positiveButton;
        private Context context = null;

        public DialogMessage(Context context, String title, String message, String positiveButton) {
            this.context = context;
            this.title = title;
            this.message = message;
            this.positiveButton = positiveButton;
        }

        public void build(){
            alertBuilder = new AlertDialog.Builder(this.context);
            alertBuilder.setTitle(this.title);
            alertBuilder
                    .setMessage(this.message)
                    .setCancelable(false)
                    .setPositiveButton(this.positiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    });
        }

        public void show(){
            AlertDialog alertDialog = this.alertBuilder.create();
            alertDialog.show();
        }
    }
}
