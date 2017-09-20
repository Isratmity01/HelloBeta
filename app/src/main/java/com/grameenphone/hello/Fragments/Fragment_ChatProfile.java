package com.grameenphone.hello.Fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.Adapter.LiveUserListAdapter;
import com.grameenphone.hello.Adapter.RoomListAdapter;
import com.grameenphone.hello.Adapter.SharedImageAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_ChatProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView userrecylcer,msgrecyler;
    private LiveUserListAdapter liveUserListAdapter;
    private ArrayList<String>receivedphotos=new ArrayList<>();
    private RoomListAdapter roomListAdapter;
    private ArrayList<Integer> userArrayList=new ArrayList<>();
    RecyclerView allusers;
    private CardView notificationCardView;
    private CardView gallaryCard;
    private DatabaseReference mFirebaseDatabaseReference;
    private CardView blockCard, deleteCard;
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
    private TextView blockedusername;
    private String mUsername;
    private Toolbar toolbar;
    ImageButton ProfileEdit;
    private GridView gridView;
    private ProgressBar progressBar;
    User sender,receiver;
    View fragmentView;
    SharedImageAdapter sharedImageAdapter;
    private TextView ImageCount;
    private String room_id,roomName,receiver_uid;
    public Fragment_ChatProfile() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
       setHasOptionsMenu(true);
        setRetainInstance(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

       ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);




       setActionBarTitle("প্রোফাইল");
        Bundle bundle = this.getArguments();
        room_id = bundle.getString("room_uid");
        roomName = bundle.getString("room_name");
        dbHelper = new DatabaseHelper(getActivity());
        sender=dbHelper.getMe();
        receiver_uid = (room_id.replace(sender.getUid(), "")).replace("_", "");
        receiver = dbHelper.getUser(receiver_uid);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
    }

    public boolean onSupportNavigateUp(){

        getActivity().getFragmentManager().popBackStack();
        return true;
    }
    public void setActionBarTitle(String title) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.activity_chat_profile,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }
    private void bindViews(View view) {
        progressBar=(ProgressBar)view.findViewById(R.id.imageloader);
        ImageCount=(TextView)view.findViewById(R.id.imagecount);
        notificationCardView = (CardView) view.findViewById(R.id.notification_card);
        gallaryCard = (CardView) view.findViewById(R.id.gallary_card);
        gridView=(GridView)view.findViewById(R.id.sharedimageholder);
        blockCard = (CardView) view.findViewById(R.id.block_card);
        deleteCard = (CardView) view.findViewById(R.id.delete_card);
        usersPhoto = (ImageView) view.findViewById(R.id.profile_picture);
        blockedusername=(TextView)view.findViewById(R.id.lable_block_about);
        blockedusername.setText("ব্লক - "+receiver.getName());

        userName = (TextView) view.findViewById(R.id.profile_name);
        userName.setText(receiver.getName());
        Glide.with(this).load( receiver.getPhotoUrl() ).bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.hello1)
                .into(usersPhoto);

        FloatingActionsMenu floatingActionsMenu=  (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        floatingActionsMenu.setVisibility(View.GONE);
        notificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });
        gallaryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        blockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });
        deleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("");
                alert.setMessage("সরি, এই ফিচারটি এখনো অ্যাভেইলেবল না");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                alert.show();
            }
        });
    }
    private void loadImages()
    {
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseDatabaseReference.child("chat_rooms").child(room_id)
                    .orderByChild("messageType").equalTo("img")

        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chatroom = dataSnapshot.getValue(Chat.class);
                receivedphotos.add(chatroom.getFile().getUrl_file());





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
        mFirebaseDatabaseReference.child("chat_rooms").child(room_id)
                .orderByChild("messageType").equalTo("img").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  System.out.println("We're done loading the initial "+dataSnapshot.getChildrenCount()+" items");
                int val= (int) dataSnapshot.getChildrenCount();
                ImageCount.setText(String.valueOf(val) );
              //  Toast.makeText(getActivity().getApplicationContext(),"লোড হয়েছে "+dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                sharedImageAdapter=new SharedImageAdapter(getActivity(),receivedphotos);

                gridView.setAdapter(sharedImageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getActivity() .getWindow();


            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        AppBarLayout appBarLayout=(AppBarLayout)getActivity().findViewById(R.id.appbarmain);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.height=186;


        params.width=ViewGroup.LayoutParams.MATCH_PARENT;;
        appBarLayout.setLayoutParams(params);
        init();
    }
    public void init()
    {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        loadImages();
        populateList();
    }
    public void populateList()
    {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here

                getActivity().onBackPressed();
                return true;
            case R.id.edit_profile:
                // Do onliTck on menu action here
                Fragment_UserProfileEdit fragment2 = new Fragment_UserProfileEdit();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment2);
                fragmentTransaction.addToBackStack("profile_edit");
                fragmentTransaction.commit();
                return true;
        }
        return false;
    }

    public void refreshlistview()
    {



    }


}
