package com.grameenphone.hello.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.User;

public class UserProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CardView notificationCardView;
    private CardView privacyCardView;
    private CardView helpAboutCardView, signoutCard;
    public static final String ANONYMOUS = "anonymous";
    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;
    private ImageView usersPhoto;
    private TextView userName;
    private TextView usersPhoneNumber;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelper dbHelper;
    private User me;
    private Switch Lanswitch;
    private String mUsername;
    private Toolbar toolbar;
    ImageButton ProfileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_user_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        final Drawable upArrow = ContextCompat.getDrawable(UserProfileActivity.this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(UserProfileActivity.this, R.color.icons), PorterDuff.Mode.SRC_ATOP);
        ab.setHomeAsUpIndicator(upArrow);
        setActionBarTitle("ইউজার প্রোফাইল");

        notificationCardView = (CardView) findViewById(R.id.notification_card);
        privacyCardView = (CardView) findViewById(R.id.privacy_card);
        helpAboutCardView = (CardView) findViewById(R.id.help_about_card);
        signoutCard = (CardView) findViewById(R.id.signout_card);
        usersPhoto = (ImageView) findViewById(R.id.profile_picture);
        userName = (TextView) findViewById(R.id.profile_name);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        dbHelper = new DatabaseHelper(getApplicationContext());
        me = dbHelper.getMe();

        if (me != null) {

            if (me.getName() != null) userName.setText(me.getName());

            if (me.getPhone() != null) usersPhoneNumber.setText(me.getPhone());

            if (me.getPhotoUrl() != null) {

              //  options.transform(new BlurTransformation(UserProfileActivity.this));
                Glide.with(UserProfileActivity.this)
                        .load(me.getPhotoUrl())
                        .into(usersPhoto);


            }
        }
        notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Intent intent = new Intent(getApplicationContext(), NotificationSettingsActivity.class);
             //   startActivity(intent);
            }
        });

        privacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      Intent intent = new Intent(getApplicationContext(), PrivacySettingsActivity.class);
            //    startActivity(intent);
            }
        });

        helpAboutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getApplicationContext(), HelpAboutActivity.class);
               // startActivity(intent);
            }
        });
        signoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finishAffinity();
                startActivity(intent);*/
            }
        });

            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:


                Intent intent = new Intent(this, ProfileUpdateActivity.class);
                intent.putExtra("name", me.getName());
                intent.putExtra("photoUrl", me.getPhotoUrl());
                startActivity(intent);
                finish();

                return true;

            case R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(" ", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
