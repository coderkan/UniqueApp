package com.eg.uniqueapp.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eg.uniqueapp.R;
import com.eg.uniqueapp.control.PhoneChecker;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity  implements ChildEventListener {

    @BindView(R.id.text_app_id)
    EditText textAppId;

    @BindView(R.id.text_log)
    TextView textLog;
    private String strLog = "";

    ArrayList<Model> list = new ArrayList<>();

    private final static String TAG = "SettingsActivity";

    private FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener = null;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Type network = Type.NONE;
    private boolean isSignin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        log(PhoneChecker.getInstance().getAndroidId());
        log(PhoneChecker.getInstance().getDeviceId());

        if(((network = NetworkChecker.isNetWorkAvailable(getApplicationContext())) == Type.NONE)){
            message("Check Your Network Connection...");
        }
        if(network != Type.NONE){
            controlAuth();
        }
        setToolBar();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab) void fabButtonClick(View view){
        //addFireBase();
        strLog = "";
        writeList();
    }

    private void writeList() {
        for(int i = 0; i < list.size(); i++){
            log(list.get(i).toString() + "\n");
        }
    }

    @OnClick(R.id.button_auth) void authButtonClick(){
        if(((network = NetworkChecker.isNetWorkAvailable(getApplicationContext())) == Type.NONE)){
            message("Check Your Network Connection...");
        }
        if(list.size() == 0){
            if(network != Type.NONE){
                controlAuth();
            }
        }
        log(textAppId.getText().toString());
    }

    private void log(String msg){
        if(msg.length() != 0)
            strLog += " :: " + msg;
        textLog.setText(strLog);
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

    private void message(String msg){
        Toast.makeText(SettingsActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }


    private void getUpdates(DataSnapshot ds) {
        list.clear();
        for(DataSnapshot data : ds.getChildren())
        {
            Model model = new Model();
            model.setApplicationId(data.getValue(Model.class).getApplicationId());
            model.setImeiId(data.getValue(Model.class).getImeiId());
            model.setImeiId(data.getValue(Model.class).getAndroidId());
            list.add(model);
            Log.e(TAG,model.toString());
        }
    }

    private void addFireBase() {
        Model model = new Model("31","321654987", "bbffddsadafdfaf" );
        root.child("Users").push().setValue(model);
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
}
