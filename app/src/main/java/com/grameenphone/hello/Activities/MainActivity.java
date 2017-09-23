package com.grameenphone.hello.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grameenphone.hello.Fragments.Fragment_Contacts;
import com.grameenphone.hello.Fragments.Fragment_Live;
import com.grameenphone.hello.Fragments.Fragment_MainPage;
import com.grameenphone.hello.Fragments.Fragment_PrivateChat;
import com.grameenphone.hello.Fragments.Fragment_UserProfile;
import com.grameenphone.hello.Fragments.Fragment_UserProfileEdit;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.EventReceived;
import com.grameenphone.hello.model.User;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final String ANONYMOUS = "anonymous";
    private static final int PERMISSION_REQUEST_CONTACT = 1033;
    public static FirebaseUser mFirebaseUser;
    public static int liveusercount;
    public User me;
    public DatabaseHelper databaseHelper;
    public ArrayList<String> finalliveusers = new ArrayList<>();
    Drawable drawable;
    boolean doubleBackToExitPressedOnce = false;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    android.support.v4.app.FragmentManager fragmentManagerP2p;
    android.support.v4.app.FragmentTransaction fragmentTransactionP2p;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReferenceForLiveCount;
    private MenuItem item;
    private String mUsername;
    private Boolean Isopened = false;
    private ImageButton toolbarIcon;
    private ImageView transparentView;
    private ImageView liveBanner;
    private FloatingActionsMenu menuMultipleActions;
    private FrameLayout frameLayout;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    public ArrayList<String> getFinalliveusers() {
        return finalliveusers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();


            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window =getWindow();
            Drawable background = getResources().getDrawable(R.drawable.gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbarcolor));
            //window.setBackgroundDrawable(background);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("weather");

        mFirebaseDatabaseReferenceForLiveCount = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setContentInsetStartWithNavigation(0);
        fragmentManager = MainActivity.this.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        databaseHelper = new DatabaseHelper(MainActivity.this);
        drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_account_circle_white_24dp);
        toolbar.setOverflowIcon(drawable);
        finalliveusers.clear();
        loadliveuserchips();
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.hellologo);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //   ab.setDisplayHomeAsUpEnabled(true);

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_new_message));

        me = databaseHelper.getMe();
        frameLayout = (FrameLayout) findViewById(R.id.totalview);
        frameLayout.getBackground().setAlpha(0);

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        menuMultipleActions.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                Isopened = true;
                frameLayout.getBackground().setAlpha(240);

            }

            @Override
            public void onMenuCollapsed() {
                Isopened = false;
                frameLayout.getBackground().setAlpha(0);
            }
        });
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (menuMultipleActions.isExpanded()) {
                    menuMultipleActions.collapse();
                    return true;
                }
                return false;
            }
        });


        //    ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        //    drawable.getPaint().setColor(getResources().getColor(R.color.white));
        //    ((FloatingActionButton) findViewById(R.id.setter_drawable)).setIconDrawable(drawable);

        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_dry_cleaning);
        actionA.setSize(FloatingActionButton.SIZE_MINI);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.getBackground().setAlpha(0);
                menuMultipleActions.collapse();
                Fragment_Live fragment22 = new Fragment_Live();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, fragment22);
                fragmentTransaction.addToBackStack("live");
                fragmentTransaction.commit();
            }
        });
        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.action_contacts);
        actionB.setSize(FloatingActionButton.SIZE_MINI);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.getBackground().setAlpha(0);
                menuMultipleActions.collapse();
                askForContactPermission();

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this /* FragmentActivity */, this  /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        loadliveUsers();


        Intent intent = getIntent();

        String roomId = intent.getStringExtra("room_uid");
        String roomName = intent.getStringExtra("room_name");

        if (roomId != null && roomName != null) {
            StartP2p(roomId, roomName);
        }


    }

    private void loadliveuserchips()

    {
        mFirebaseDatabaseReferenceForLiveCount.child("live_user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (!finalliveusers.contains(dataSnapshot.getKey())) {
                    {

                        finalliveusers.add(dataSnapshot.getKey());


                    }

                    liveusercount++;
                    if (Fragment_Live.isActive) {
                        EventBus.getDefault().post(new EventReceived(true, dataSnapshot.getKey()));
                    } else {
                        EventBus.getDefault().post(new EventReceived(true, dataSnapshot.getKey()));
                    }

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                finalliveusers.remove(dataSnapshot.getKey());
                liveusercount--;
                if (Fragment_Live.isActive) {
                    EventBus.getDefault().post(new EventReceived(false, dataSnapshot.getKey()));
                } else
                    EventBus.getDefault().post(new EventReceived(false, dataSnapshot.getKey()));
                //  EventBus.getDefault().postSticky(new EventReceived(false, liveUser));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadContacts() {
        Fragment_Contacts fragment_contacts = new Fragment_Contacts();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment_contacts);
        fragmentTransaction.addToBackStack("contacts");
        fragmentTransaction.commit();
    }

    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                loadContacts();
            }
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void loadliveUsers() {


        Fragment_MainPage fragment = new Fragment_MainPage();

        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("msgs");
        fragmentTransaction.commit();
        /*
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("users_chat_room").child("9htn5aIb9wfxDB6B52sUOZE7xIU2").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                //  dbHelper.addRoom(chatroom.getRoomId(), chatroom.getName(), chatroom.getPhotoUrl(), chatroom.getType());
                if(chatroom.getType().equals("p2p")){
                    userArrayList.add(chatroom);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseDatabaseReference.child("users_chat_room").child("9htn5aIb9wfxDB6B52sUOZE7xIU2").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                Toast.makeText(MainActivity.this,"লোড হয়েছে " +dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();
                try {

                    liveUserListAdapter=new LiveUserListAdapter(MainActivity.this,userArrayList);
                    userrecylcer.setAdapter(liveUserListAdapter);
                    //userrecylcer.setOnClickListener((View.OnClickListener) getActivity().getApplicationContext());
                }catch (Exception e)
                {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });*/
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof Fragment_MainPage) {

            if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "অ্যাপ বন্ধ করতে আরেকবার ব্যাক চাপুন", Toast.LENGTH_SHORT).show();


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {

                finishAffinity();
            }//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");


        } else if (f instanceof Fragment_UserProfile) {
            menuMultipleActions.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();

            ab.setTitle("");
            ab.setDisplayUseLogoEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);

            super.onBackPressed();//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else if (f instanceof Fragment_Contacts) {
            menuMultipleActions.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();

            ab.setTitle("");
            ab.setDisplayUseLogoEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);

            super.onBackPressed();//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else if (f instanceof Fragment_PrivateChat) {
            menuMultipleActions.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();

            ab.setTitle("");
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.drawable.hellologo);
            ab.setDisplayHomeAsUpEnabled(false);
            super.onBackPressed();
            //the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else if (f instanceof Fragment_Live) {
            mFirebaseDatabaseReferenceForLiveCount.child("live_user").child(me.getUid()).setValue(null);

            menuMultipleActions.setVisibility(View.VISIBLE);
            ActionBar ab = getSupportActionBar();

            ab.setTitle("");
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.drawable.hellologo);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setSubtitle("");

            super.onBackPressed();//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else if (f instanceof Fragment_UserProfileEdit) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("ইউজার প্রোফাইল");
            ab.setDisplayHomeAsUpEnabled(true);
            super.onBackPressed();//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else if (f instanceof Fragment_Contacts) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle("");
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayUseLogoEnabled(true);
            ab.setLogo(R.drawable.hellologo);
            super.onBackPressed();//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        item = menu.findItem(R.id.action_settings);
        Glide.with(this).load(me.getPhotoUrl()).asBitmap().centerCrop().transform(new CropCircleTransformation(MainActivity.this)).into(new SimpleTarget<Bitmap>(50, 50) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                item.setIcon(new BitmapDrawable(getResources(), resource));

            }
        });
        return true;
    }

    public void SignOut() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        mUsername = ANONYMOUS;
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            frameLayout.getBackground().setAlpha(0);
            menuMultipleActions.collapse();
            Fragment_UserProfile fragment2 = new Fragment_UserProfile();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment2);
            fragmentTransaction.addToBackStack("profile");
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void StartP2p(String roomId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("room_uid", roomId);
        bundle.putString("room_name", name);

        Fragment_PrivateChat fragmentp = new Fragment_PrivateChat();

        fragmentp.setArguments(bundle);


        fragmentManagerP2p = MainActivity.this.getSupportFragmentManager();
        fragmentTransactionP2p = fragmentManagerP2p.beginTransaction();

        fragmentTransactionP2p.replace(R.id.fragment_container, fragmentp);
        fragmentTransactionP2p.addToBackStack("p2p");
        fragmentTransactionP2p.commit();


    }


}
