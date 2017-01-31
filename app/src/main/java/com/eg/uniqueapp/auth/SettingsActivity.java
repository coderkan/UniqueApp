package com.eg.uniqueapp.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eg.uniqueapp.R;
import com.eg.uniqueapp.model.Model;
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

public class SettingsActivity extends AppCompatActivity  implements ChildEventListener , SettingsView{

    @BindView(R.id.text_app_id)
    TextInputEditText textAppId;

    @BindView(R.id.email_text)
    TextInputEditText textEmail;

    @BindView(R.id.til1)
    TextInputLayout emailLayout;

    @BindView(R.id.til2)
    TextInputLayout appIdLayout;


    ArrayList<Model> list = new ArrayList<>();

    private final static String TAG = "SettingsActivity";

    private FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener = null;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private String refKey = "";
    private Model referenceModel = null;
    private SettingsPresenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        presenter = new SettingsPresenter(getApplicationContext(),this);
        presenter.init();
        presenter.loadDeviceInfo();
        presenter.loadRef();
        emailLayout.setError("Bu alan gereklidir.");
        appIdLayout.setError("Bu alan gereklidir.");
        setToolBar();
    }
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab) void fabButtonClick(View view){
        //addFireBase();
        //updateFireBase();
    }

    @OnClick(R.id.button_auth) void authButtonClick(){
        presenter.auth(textEmail.getText().toString().trim() , textAppId.getText().toString().trim(), this.list);
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
        presenter.getUpdates(ds);
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
    public void onOpenAuth() {
        controlAuth();
    }

    @Override
    public void onShowMessage(String msg) {
        message(msg);
    }

    @Override
    public void onLoadModel(Model model){
        this.referenceModel = model;
    }

    @Override
    public void onLoadFirebase(Model lmodel, Model refModel) {
        root.child("Users/"+lmodel.getRefKey()).setValue(refModel);
    }

    @Override
    public void onLoadUpdates(ArrayList<Model> list) {
        this.list.clear();
        this.list = list;
    }
}
