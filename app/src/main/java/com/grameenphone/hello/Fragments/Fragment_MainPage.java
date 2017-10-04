package com.grameenphone.hello.Fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.Adapter.IncomingChatRequestsAdapter;
import com.grameenphone.hello.Adapter.LiveUserListAdapter;
import com.grameenphone.hello.Adapter.RoomListAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.Utils.EngBng;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.events.PushNotificationEvent;
import com.grameenphone.hello.fcm.FcmNotificationBuilder;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.ChatSent;
import com.grameenphone.hello.model.EventReceived;
import com.grameenphone.hello.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.grameenphone.hello.Utils.Userlevels.getLevelName;


public class Fragment_MainPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView userrecylcer, msgrecyler, chatReqrecyler;
    private LiveUserListAdapter liveUserListAdapter;

    private RoomListAdapter roomListAdapter;
    private IncomingChatRequestsAdapter chatRequestsAdapter;
    private Handler handler;
    private int width=50,height=50;
    private CardView cardView;
    FragmentManager fragmentManager;
    ArrayList<String> liveUser = new ArrayList<String>();
    private static int liveusercount;

    private ArrayList<ChatRoom> userArrayList = new ArrayList<>();
    private ArrayList<ChatRoom> chatRequests = new ArrayList<>();

    private static ArrayList<ChatRoom> chatRooms = new ArrayList<>();
    RecyclerView allusers;
    private MenuItem item;
    private float density;
    private Toolbar toolbar;
    private TextView liveHeader,msgreqHeader;
    private DatabaseReference mFirebaseDatabaseReference, mFirebaseDatabaseReferenceForRequest, mFirebaseDatabaseReferenceForLiveCount;
    View fragmentView;
    private DatabaseHelper databaseHelper;
    private User myself;
    private TextView onlineUserCount;

    private JSONObject chatrequestobject = new JSONObject();

    public Fragment_MainPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mFirebaseDatabaseReferenceForLiveCount = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReferenceForRequest = FirebaseDatabase.getInstance().getReference();

       // ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.hellologo);

        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EventBus.getDefault().register(this);
    //    setActionBarTitle("");


    }

    public boolean onSupportNavigateUp() {
        getActivity().getFragmentManager().popBackStack();
        return true;
    }

    public void setActionBarTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null) {

            fragmentView = inflater.inflate(R.layout.content_main,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        item = menu.findItem(R.id.action_settings);
        Glide.with(this).load(myself.getPhotoUrl()).asBitmap().centerCrop()
                .transform(new CropCircleTransformation(getActivity()))
                .into(new SimpleTarget<Bitmap>(width, height) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        item.setIcon(new BitmapDrawable(getResources(), resource));

                    }
                });

    }

    private void bindViews(View view) {

            toolbar=(Toolbar)getActivity().findViewById(R.id.toolbar);
        onlineUserCount = (TextView) view.findViewById(R.id.onlineusers);
        liveusercount = 0;

        liveHeader = (TextView) view.findViewById(R.id.liveuserheader);

        msgreqHeader = (TextView) view.findViewById(R.id.incoming_chat_request_header);

        userrecylcer = (RecyclerView) view.findViewById(R.id.horizontallayoutholder);
        msgrecyler = (RecyclerView) view.findViewById(R.id.friendListRecyclerView);
        chatReqrecyler = (RecyclerView) view.findViewById(R.id.incoming_chat_request_recyclerView);


        cardView = (CardView) view.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mid = ((MainActivity) getActivity()).me.getUid();
                mFirebaseDatabaseReferenceForLiveCount.child("live_user").child(mid).setValue("true");

                // For removing from live
                // mFirebaseDatabaseReferenceForLiveCount.child("live_user")
                // .child(((MainActivity) getActivity()).me.getUid()).setValue(null);


                Fragment_Live fragment4 = new Fragment_Live();
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment4);
                fragmentTransaction.addToBackStack("live");
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getActivity().getWindow();


            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarmain);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        density = getResources().getDisplayMetrics().density;
        if(density<=1.5)
        {
            params.height = 116;
        } else if (density > 1.5 && density < 2.5) {
            params.height = 156;
        }
        else {
            params.height = 216;
            width=70;
            height=70;
        }
        //this will be changed based on device dpi


        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        toolbar.setLogo(R.drawable.hellologo);
        appBarLayout.setLayoutParams(params);
        databaseHelper = new DatabaseHelper(getActivity());
        myself = databaseHelper.getMe();

        init();
    }


    public void openDialogue(final User user, int reqStatus) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.req_layout, null);
        TextView name = (TextView) view.findViewById(R.id.profile_name);
        TextView level = (TextView) view.findViewById(R.id.level);
        TextView point = (TextView) view.findViewById(R.id.point);
        int points=user.getUserpoint();
        name.setText(user.getName());
        level.setText(getLevelName(points));
        point.setText("পয়েন্ট : "+ EngBng.EngBng(String.valueOf(points)));
        name.setText(user.getName());
        ImageView profile = (ImageView) view.findViewById(R.id.profile_picture);
        Glide.with(getActivity()).load(user.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.hellosmall)
                .into(profile);
        alertadd.setView(view);


        if (reqStatus == 1) {

            final String chatRoomId = Compare.getRoomName(user.getUid(), myself.getUid());
            final ChatRoom chatRoom = databaseHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("মেসেজ পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartP2p(chatRoomId, chatRoom.getName());
                    dialog.dismiss();
                }
            });


        } else if (reqStatus == 2) {

            final String chatRoomId = Compare.getRoomName(user.getUid(), myself.getUid());
            final ChatRoom chatRoom = databaseHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("মেসেজ ⁠⁠⁠রিকোয়েস্ট এক্সেপ্ট করুন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chatRoom.setRequestStatus(1);

                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(myself.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoom);


                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(myself.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(myself.getPhotoUrl());
                    chatRoomForSender.setRequestStatus(1);


                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(user.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoomForSender);



                    try {
                        String message = "মেসেজ ⁠⁠⁠রিকোয়েস্ট এক্সেপ্ট করেছেন";
                        chatrequestobject = populateJsonChat(chatrequestobject, myself.getName(), message, myself.getName());
                        sendPushNotificationToRequestReceiver(chatrequestobject, myself.getFirebaseToken(), user.getFirebaseToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    StartP2p(chatRoomId, chatRoom.getName());
                    dialog.dismiss();
                }
            });

        } else if (reqStatus == 0) {

            alertadd.setPositiveButton(Html.fromHtml("<font color='#8190a7'>মেসেজ ⁠⁠⁠রিকোয়েস্ট পাঠিয়েছেন</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        } else {

            alertadd.setPositiveButton("মেসেজ রিকোয়েস্ট পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String chatRoomId = Compare.getRoomName(user.getUid(), myself.getUid());
                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(myself.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(myself.getPhotoUrl());
                    chatRoomForSender.setRequestStatus(2);


                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(user.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoomForSender);


                    final ChatRoom chatRoomForMe = new ChatRoom();

                    chatRoomForMe.setName(user.getName());
                    chatRoomForMe.setRoomId(chatRoomId);
                    chatRoomForMe.setPhotoUrl(user.getPhotoUrl());
                    chatRoomForMe.setRequestStatus(0);

                    databaseHelper.addRoom(chatRoomId, user.getName(), user.getPhotoUrl(), 0);


                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(myself.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoomForMe);


                    try {
                        String message = "মেসেজ ⁠⁠⁠রিকোয়েস্ট পাঠিয়েছেন";
                        chatrequestobject = populateJsonChat(chatrequestobject, myself.getName(), message, myself.getName());
                        sendPushNotificationToRequestReceiver(chatrequestobject, myself.getFirebaseToken(), user.getFirebaseToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();
                }
            });

        }


        alertadd.show();

    }


    public void StartP2p(String roomId, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("room_uid", roomId);
        bundle.putString("room_name", name);

        Fragment_PrivateChat fragmentp = new Fragment_PrivateChat();

        fragmentp.setArguments(bundle);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentp);
        fragmentTransaction.addToBackStack("p2p");
        fragmentTransaction.commit();
        //    transaction.addToBackStack(null);


    }

    public void init() {

        userArrayList.clear();
        userArrayList.addAll(databaseHelper.getAllRoombyStatus(1));


        chatRequests.clear();
        chatRequests.addAll(databaseHelper.getAllRoombyStatus(2));


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("users_chat_room").child(((MainActivity) getActivity()).me.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                        if (chatroom != null) {
                            if(chatroom.getRoomId()!=null) {

                                databaseHelper.addRoom(chatroom.getRoomId(),
                                        chatroom.getName(),
                                        chatroom.getPhotoUrl(),
                                        chatroom.getRequestStatus());

                                if (chatroom.getRequestStatus() == 1) {
                                    userArrayList.add(chatroom);
                                } else if (chatroom.getRequestStatus() == 2) {
                                    chatRequests.add(chatroom);
                                }


                                userArrayList.clear();
                                userArrayList.addAll(databaseHelper.getAllRoombyStatus(1));


                                chatRequests.clear();
                                chatRequests.addAll(databaseHelper.getAllRoombyStatus(2));
                            }

                        }

                        roomListAdapter.notifyDataSetChanged();
                        chatRequestsAdapter.notifyDataSetChanged();


                        if(chatRequests.size() > 0) {
                            msgreqHeader.setVisibility(View.VISIBLE);
                        } else {
                            msgreqHeader.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);

                        if (chatroom != null) {

                            if (chatroom.getRequestStatus() == 1) {

                                databaseHelper.addRoom(chatroom.getRoomId(),
                                        chatroom.getName(),
                                        chatroom.getPhotoUrl(),
                                        chatroom.getRequestStatus());

                                userArrayList.clear();
                                userArrayList.addAll(databaseHelper.getAllRoombyStatus(1));


                                chatRequests.clear();
                                chatRequests.addAll(databaseHelper.getAllRoombyStatus(2));
                            }

                        }

                        roomListAdapter.notifyDataSetChanged();
                        chatRequestsAdapter.notifyDataSetChanged();




                        if(chatRequests.size() > 0) {
                            msgreqHeader.setVisibility(View.VISIBLE);
                        } else {
                            msgreqHeader.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);

                        if (chatroom != null && chatroom.getRoomId() != null) {
                            databaseHelper.addRoom(chatroom.getRoomId(),
                                    chatroom.getName(),
                                    chatroom.getPhotoUrl(),
                                    100);

                            userArrayList.clear();
                            userArrayList.addAll(databaseHelper.getAllRoombyStatus(1));


                            chatRequests.clear();
                            chatRequests.addAll(databaseHelper.getAllRoombyStatus(2));


                        }

                        roomListAdapter.notifyDataSetChanged();
                        chatRequestsAdapter.notifyDataSetChanged();

                        if(chatRequests.size() > 0) {
                            msgreqHeader.setVisibility(View.VISIBLE);
                        } else {
                            msgreqHeader.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        roomListAdapter = new RoomListAdapter(getActivity(), userArrayList, Fragment_MainPage.this,databaseHelper,myself);
        msgrecyler.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        msgrecyler.setLayoutManager(layoutManager);
        msgrecyler.setAdapter(roomListAdapter);


        chatRequestsAdapter = new IncomingChatRequestsAdapter(getActivity(), chatRequests, Fragment_MainPage.this, myself);
        chatReqrecyler.setNestedScrollingEnabled(false);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);

        chatReqrecyler.setLayoutManager(llm);
        chatReqrecyler.setAdapter(chatRequestsAdapter);


        if (chatRequests.size() > 0) msgreqHeader.setVisibility(View.VISIBLE);



        LiveChips();
        handler = new Handler();



        handler.post(runnableCode);

    }
    private Runnable runnableCode = new Runnable() {

        @Override

        public void run() {



            for(ChatRoom chatRoom : userArrayList){

                if(chatRoom.getRoomId() != null) {

                    Chat chat = databaseHelper.getLastMsg(chatRoom.getRoomId());

                    if(chat != null && chat.getMessage() !=null && chat.getTimestamp() != 0){

                        chatRoom.setLastChat(chat.getMessage());

                        chatRoom.setTimestamp(chat.getTimestamp());

                    }




                }

            }





            Collections.sort( userArrayList, compareChatroom );

            roomListAdapter.notifyDataSetChanged();





            handler.postDelayed(this, 2000);

        }

    };
    private void LiveChips() {

       liveUser=((MainActivity)getActivity()).getFinalliveusers();
        liveUserListAdapter = new LiveUserListAdapter(
                getActivity(),
                liveUser,
                databaseHelper,
                Fragment_MainPage.this);

        userrecylcer.setAdapter(liveUserListAdapter);
        liveUserListAdapter.notifyDataSetChanged();
        onlineUserCount.setText(EToB(String.valueOf(liveUser.size())) + " জন অনলাইনে আছে");
        if (liveUser.size() > 0) liveHeader.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onEvent(EventReceived event) {

        if (event.isLoginSuccessful()) {
            if (liveUser.contains(event.getResponseMessage())) {
                liveUser.remove(event.getResponseMessage());
            }
            liveUser.add(0, event.getResponseMessage());
            liveHeader.setVisibility(View.VISIBLE);
            //setLiveusercount(liveusercount=liveUser.size());
            liveusercount++;
            onlineUserCount.setText(EToB(String.valueOf(liveUser.size())) + " জন অনলাইনে আছে");
            liveUserListAdapter.notifyDataSetChanged();

        } else {
            liveUser.remove(event.getResponseMessage());
            liveusercount--;
            onlineUserCount.setText(EToB(String.valueOf(liveUser.size())) + " জন অনলাইনে আছে");
            if (liveUser.size() == 0) liveHeader.setVisibility(View.GONE);
            //  setLiveusercount(liveusercount=liveUser.size());
            // liveUser.remove(event.getResponseMessage());
            liveUserListAdapter.notifyDataSetChanged();

        }

        //check if login was successful

    }

    @Override
    public void onResume() {
        super.onResume();

    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }*/

    private String EToB(String english_number) {
        if (english_number.equals("null") || english_number.equals(""))
            return english_number;
        int v = english_number.length();
        String concatResult = "";
        for (int i = 0; i < v; i++) {
            if (english_number.charAt(i) == '1')
                concatResult = concatResult + "১";
            else if (english_number.charAt(i) == '2')
                concatResult = concatResult + "২";
            else if (english_number.charAt(i) == '3')
                concatResult = concatResult + "৩";
            else if (english_number.charAt(i) == '4')
                concatResult = concatResult + "৪";
            else if (english_number.charAt(i) == '5')
                concatResult = concatResult + "৫";
            else if (english_number.charAt(i) == '6')
                concatResult = concatResult + "৬";
            else if (english_number.charAt(i) == '7')
                concatResult = concatResult + "৭";
            else if (english_number.charAt(i) == '8')
                concatResult = concatResult + "৮";
            else if (english_number.charAt(i) == '9')
                concatResult = concatResult + "৯";
            else if (english_number.charAt(i) == '0')
                concatResult = concatResult + "০";

            else {
                return english_number;
            }

        }
        return concatResult;
    }

    public static Comparator<ChatRoom> compareChatroom =

            new Comparator<ChatRoom>() {

                public int compare(ChatRoom chat1, ChatRoom other) {

                    return Long.compare(other.getTimestamp(),chat1.getTimestamp());

                }

            };



    private JSONObject populateJsonChat (JSONObject chatrequest, String sender, String message, String title) throws JSONException {
        chatrequest.put("sender", sender);
        chatrequest.put("text", message);
        chatrequest.put("title", title);

        return chatrequest;
    }




    private void sendPushNotificationToRequestReceiver(JSONObject chatRequest,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        //   EventBus.getDefault().post(new ChatSent("yes"));
        FcmNotificationBuilder.initialize()
                .notificationType("request")
                .setReceived(chatRequest)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {

        chatRooms.clear();

        //chatRooms.addAll(populateChatRoomArraylist());
        roomListAdapter.refresh();
    }
    @Subscribe
    public void onEvent(ChatSent event){
        // your implementation
        chatRooms.clear();
        //chatRooms.addAll(populateChatRoomArraylist());
        roomListAdapter.refresh();

    }


}
