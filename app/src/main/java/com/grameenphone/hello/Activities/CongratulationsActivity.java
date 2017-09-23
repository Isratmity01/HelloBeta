package com.grameenphone.hello.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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


public class CongratulationsActivity extends Activity {


    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private User user;
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


                                        if (user != null && user.getUid().equals(mFirebaseUser.getUid())) {
                                            dbHelper.addMe(user);
                                        } else {
                                            dbHelper.addUser(user);
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
