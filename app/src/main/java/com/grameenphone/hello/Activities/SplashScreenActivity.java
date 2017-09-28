package com.grameenphone.hello.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.User;


public class SplashScreenActivity extends Activity
{

    private static long SPLASH_MILLIS = 1200;

    private String mClassToLaunchPackage;

    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;

    private DatabaseReference mFirebaseDatabaseReference;

    private static User me;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         prefs = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_splash_screen, null, false);

        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mClassToLaunchPackage = getPackageName();


        dbHelper = new DatabaseHelper(getApplicationContext());
        float density=getResources().getDisplayMetrics().density;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();




        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {


                if (mFirebaseUser == null) {
                    // Not signed in, launch the Sign In activity

                    Boolean yourLocked = prefs.getBoolean("locked", false);
                    if(yourLocked)
                    {

                        startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
                    }
                    else {

                        startActivity(new Intent(SplashScreenActivity.this, PinActivity.class));
                    }
                    finish();
                    return;
                } else {










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












                    me = dbHelper.getMe();

                    if(me.getName() != null ) {
                        startMainActivity();
                    } else {

                        startActivity(new Intent(SplashScreenActivity.this, ProfileEditActivity.class));
                        finish();
                        return;
                    }

                }


            }

        }, SPLASH_MILLIS);


    }
    private void startMainActivity()
    {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
