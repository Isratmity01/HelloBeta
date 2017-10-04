package com.grameenphone.hello.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CongratulationsActivity extends Activity {


    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User user;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS=1094;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseHelper dbHelper;
    private Button profile;
    private ImageView top;
    private ProgressBar mProgressBar;


    private static final String TAG = "CongratulatinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_congratulations);
        top = (ImageView)findViewById(R.id.success_icon);
        top.setImageResource(R.drawable.succ);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkPermissions();

            //user is using app for the first time

        }


        profile = (Button) findViewById(R.id.profile);
        profile.setBackgroundResource(R.drawable.profile_button_shape);
        profile.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarCongratulation);



        dbHelper = new DatabaseHelper(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();



        if(mFirebaseUser != null){
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        user = dataSnapshot.getValue(User.class);
                        if(user!=null){

                            user.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());
                            mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).setValue(user);
                            mFirebaseDatabaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot child: dataSnapshot.getChildren()) {

                                        User user = child.getValue(User.class);

                                        if (user != null &&
                                                user.getUid() != null &&
                                                user.getName() != null &&
                                                user.getUid().equals( mFirebaseUser.getUid() )) {

                                            dbHelper.addMe(user);
                                        } else {
                                            if(user != null && user.getUid() != null && user.getName() != null ) {
                                                dbHelper.addUser(user);
                                            }
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            profile.setText("হোম স্ক্রিনে যান");
                        }

                    }
                    mProgressBar.setVisibility(View.GONE);
                    profile.setVisibility(View.VISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user != null){

                    dbHelper.addMe(user);

                    Intent intent = new Intent(CongratulationsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    Intent intent = new Intent(CongratulationsActivity.this, ProfileEditActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });



    }
    private void RequestPermissionDialogue(final String [] params){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getResources().getText(R.string.hellodisclaimer));
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "ঠিক আছে",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                        }
                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private void checkPermissions() {

        List<String> permissions = new ArrayList<>();

        String message = "hello permissions:";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            permissions.add(Manifest.permission.READ_CONTACTS);

            message += "\n to get contacts from phone.";

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            message += "\nfor accessing gallery";

            //requestReadPhoneStatePermission();
        }

        if (!permissions.isEmpty()) {

            // Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            String[] params = permissions.toArray(new String[permissions.size()]);
            RequestPermissionDialogue(params);


        } // else: We already have permissions, so handle as normal

    }
    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();

                // Initial

                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);



                // Fill with results

                for (int i = 0; i < permissions.length; i++)

                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE



                Boolean contactstate = perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

                Boolean writestate = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if ( contactstate && writestate) {

                    // All Permissions Granted

Toast.makeText(CongratulationsActivity.this, "Thanks for permission", Toast.LENGTH_SHORT).show();

                    return;



                }

            }

            break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }
    @Override
    protected void onResume() {
        super.onResume();



    }
    @Override
    protected void onStart() {
        super.onStart();


    }
    @Override
    protected void onRestart() {
        super.onRestart();


    }











}
