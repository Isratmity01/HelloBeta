package com.grameenphone.hello.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cipherthinkers.shapeflyer.ShapeFlyer;
import com.cipherthinkers.shapeflyer.flyschool.FlyBluePrint;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.Adapter.LiveListAdapter;
import com.grameenphone.hello.Adapter.LiveUserListAdapterInside;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.Utils.EngBng;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.EventReceived;
import com.grameenphone.hello.model.EventReceived2;
import com.grameenphone.hello.model.FileModel;
import com.grameenphone.hello.model.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.StickerGridView;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;
import static com.grameenphone.hello.Utils.Userlevels.getLevelName;


/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_Live extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "mars_live";




    FragmentManager fragmentManager;

    private DatabaseReference mFirebaseDatabaseReference, mFirebaseDatabaseReferenceForRequest, mFirebaseDatabaseReferenceForLiveCount;

    private ImageView mSendButton;
    private RelativeLayout msendback;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager, linearLayoutManager;
    private int initial = 100;
    private int eachtime = 50;
    private String receivedKey;
    private Button jumpToBottom;
    private ArrayList<Integer> userArrayList2 = new ArrayList<>();
    private View rootView;
    EmojiconEditText emojiconEditText;
    public static Boolean isActive = false;
    private User sender;

    private DatabaseHelper dbHelper;

    private ImageButton Back;
    private int loadCalled = 0;

    ArrayList<String> liveUserinside = new ArrayList<String>();
    private static final String TAG = Fragment_Live.class.getSimpleName();
    private ProgressDialog progressDialog;

    private static final int IMAGE_GALLERY_REQUEST = 101;
    private View borderbottom;

    int eachreduced = eachtime;
    private ArrayList<Chat> userArrayLiveList = new ArrayList<>();
    public static FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Toolbar toolbarlive;
    private LiveListAdapter chatLiveAdapter;
    private ProgressBar LivePrg;
    private ImageButton attach;

    private RecyclerView userrecylcerinside;
    private LiveUserListAdapterInside liveUserListAdapterInside;
    private View fragmentView;
    private Button LoadMsg;
    ShapeFlyer mShapeFlyer;
    private TextView titleText,livepeople;
    private ImageView receiverPhoto;
    private FlyBluePrint linearBluePrint;
    private static int liveusercount;

    public static int getLiveusercount() {
        return liveusercount;
    }

    public static void setLiveusercount(int liveusercount) {
        Fragment_Live.liveusercount = liveusercount;
    }

    public Fragment_Live() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            Drawable background = getResources().getDrawable(R.drawable.gradient);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbarcolor));
            //window.setBackgroundDrawable(background);
        }
        EventBus.getDefault().register(this);
        mFirebaseDatabaseReferenceForLiveCount = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReferenceForRequest = FirebaseDatabase.getInstance().getReference();


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);
        setRetainInstance(true);
       // ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
     //   ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backiconsmall);



        setActionBarTitle("লাইভ");
    }


    public void setActionBarTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

    }

    public void setActionBarSubTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(title + " জন একটিভ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // TODO Add your menu entries here


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null) {


            fragmentView = inflater.inflate(R.layout.fragment_mars_live,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }

    private void bindViews(View view) {

        LivePrg = (ProgressBar) view.findViewById(R.id.liveprogress);
        userrecylcerinside = (RecyclerView) view.findViewById(R.id.horizontallayoutholder2);
        toolbarlive = (Toolbar) getActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoadMsg = (Button) view.findViewById(R.id.jump_totop);
        LoadMsg.setVisibility(View.GONE);

        attach = (ImageButton) view.findViewById(R.id.attachment);
        attach.setBackgroundResource(R.drawable.ic_attach);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();
                //mShapeFlyer.startAnimation(R.drawable.laila);
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbarmain);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        float density = getResources().getDisplayMetrics().density;
        if(density<=1.5)
        {
            params.height = 80;
        }
        else  if(density>1.5 && density<2.5)
        {
            params.height = 106;
        }
        else
            params.height=156;


        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        appBarLayout.setLayoutParams(params);


        emojiconEditText = (EmojiconEditText) view.findViewById(R.id.messageEditText);
        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        sender = dbHelper.getMe();
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.mars_live_chat_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        jumpToBottom = (Button) view.findViewById(R.id.jump_bottom);
        jumpToBottom.setVisibility(View.GONE);

        rootView = (View) view.findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) view.findViewById(R.id.emoticon);
        emojiButton.setImageResource(R.drawable.emoticons);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity().getApplicationContext());
        popup.setBackgroundDrawable(null);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.emoticons);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon.getEmoji().contains("golu") || emojicon.getEmoji().contains("sticker")) {
                    if (emojicon.getEmojiId() == 0) {
                        long time = System.currentTimeMillis();
                        final Chat chat = new Chat(sender.getName(), sender.getUid(),
                                String.valueOf(emojicon.getEmoji()),
                                time, sender.getPhotoUrl(), "stk");


                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        return;
                    } else
                        return;
                }
                if (emojicon.getEmoji().contains("golu") || emojicon.getEmoji().contains("sticker")) {

                } else {
                    int start = emojiconEditText.getSelectionStart();
                    int end = emojiconEditText.getSelectionEnd();
                    if (start < 0) {
                        emojiconEditText.append(emojicon.getEmoji());
                    } else {
                        emojiconEditText.getText().replace(Math.min(start, end),
                                Math.max(start, end), emojicon.getEmoji(), 0,
                                emojicon.getEmoji().length());
                    }
                }
            }
        });
        popup.setEmojiClickListener(new StickerGridView.OnStickerClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });
        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {

                    mSendButton.setVisibility(View.VISIBLE);
                    msendback.setBackgroundResource(R.drawable.circular_shape);

                    mSendButton.setEnabled(true);
                    msendback.setEnabled(true);
                    mSendButton.setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));


                } else {
                    msendback.setEnabled(false);
                    msendback.setBackgroundResource(R.drawable.circular_shape);
                    mSendButton.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emojiconEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (popup.isShowing()) {
                    popup.dismiss();
                    //   popup.showAtBottom();
                    changeEmojiKeyboardIcon(emojiButton, R.drawable.emoticons);
                    //If keyboard is visible, simply show the emoji popup
                  /*  if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {

                    }*/
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });


        mSendButton = (ImageView) view.findViewById(R.id.send_button);
        mSendButton.setEnabled(false);
        //mSendButton.setColorFilter(  ContextCompat.getColor(getActivity().getApplicationContext(),R.color.white)  );

        msendback = (RelativeLayout) view.findViewById(R.id.sendbackground);
        msendback.setBackgroundResource(R.drawable.circular_shape);
        msendback.setEnabled(false);
        msendback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatText = emojiconEditText.getText().toString();

                long time = System.currentTimeMillis();
                Chat chat = new Chat(sender.getName(),
                        sender.getUid(),
                        chatText,
                        time, sender.getPhotoUrl(), "txt");


                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD)
                        .push().setValue(chat);
                emojiconEditText.setText("");
            }
        });


    }

    @Subscribe
    public void onEvent(EventReceived2 event) {

        if (event.isLoginSuccessful()) {
            if (liveUserinside.contains(event.getResponseMessage())) {
                liveUserinside.remove(event.getResponseMessage());
            }
            liveUserinside.add(event.getResponseMessage());
            //setLiveusercount(liveusercount=liveUser.size());
            liveusercount++;
            livepeople.setText(EToB(String.valueOf(liveUserinside.size()))+" জন একটিভ");
            userrecylcerinside.smoothScrollToPosition(liveUserinside.size() - 1);
            liveUserListAdapterInside.notifyDataSetChanged();

        } else {
            liveUserinside.remove(event.getResponseMessage());
            liveusercount--;
            livepeople.setText(EToB(String.valueOf(liveUserinside.size()))+" জন একটিভ");
            //  setLiveusercount(liveusercount=liveUser.size());
            // liveUser.remove(event.getResponseMessage());
            userrecylcerinside.smoothScrollToPosition(liveUserinside.size() - 1);
            liveUserListAdapterInside.notifyDataSetChanged();

        }

        //check if login was successful

    }

    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        floatingActionsMenu.setVisibility(View.GONE);



        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.p2pactionbar, null);
        toolbarlive.addView(mCustomView,0);
        titleText=(TextView)toolbarlive.findViewById(R.id.action_bar_title_1);
        livepeople=(TextView)toolbarlive.findViewById(R.id.action_bar_title_2);
        livepeople.setVisibility(View.VISIBLE);
        receiverPhoto=(ImageView)toolbarlive.findViewById(R.id.conversation_contact_photo);
        receiverPhoto.setImageResource(R.drawable.ic_trending_up_white_18dp);
        titleText.setText("হ্যালো লাইভ");
        toolbarlive.setContentInsetsAbsolute(0,0);
        toolbarlive.setContentInsetStartWithNavigation(0);
        toolbarlive.setOverflowIcon(null);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarlive);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),"Sorry!",Toast.LENGTH_SHORT).show();
        }

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

            final String chatRoomId = Compare.getRoomName(user.getUid(), sender.getUid());
            final ChatRoom chatRoom = dbHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("মেসেজ পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartP2p(chatRoomId, chatRoom.getName());
                    dialog.dismiss();
                }
            });


        } else if (reqStatus == 2) {

            final String chatRoomId = Compare.getRoomName(user.getUid(), sender.getUid());
            final ChatRoom chatRoom = dbHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("মেসেজ রিকোয়েস্ট এক্সেপ্ট করুন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chatRoom.setRequestStatus(1);

                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(sender.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoom);


                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(sender.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(sender.getPhotoUrl());
                    chatRoomForSender.setRequestStatus(1);


                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(user.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoomForSender);


                    StartP2p(chatRoomId, chatRoom.getName());
                    dialog.dismiss();
                }
            });

        } else if (reqStatus == 0) {

            alertadd.setPositiveButton("মেসেজ রিকোয়েস্ট পাঠিয়েছেন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        } else {

            alertadd.setPositiveButton("মেসেজ রিকোয়েস্ট পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String chatRoomId = Compare.getRoomName(user.getUid(), sender.getUid());
                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(sender.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(sender.getPhotoUrl());
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

                    dbHelper.addRoom(chatRoomId, user.getName(), user.getPhotoUrl(), 0);


                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(sender.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoomForMe);


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


        liveUserinside = ((MainActivity) getActivity()).getFinalliveusers();

        livepeople.setText(EToB(String.valueOf(liveUserinside.size()))+" জন একটিভ");
        liveUserListAdapterInside = new LiveUserListAdapterInside(getActivity(), liveUserinside, ((MainActivity) getActivity()).databaseHelper, Fragment_Live.this);
        userrecylcerinside.smoothScrollToPosition(0);
        userrecylcerinside.setAdapter(liveUserListAdapterInside);

/*
      mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child("live_user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if ( !liveUser.contains(dataSnapshot.getKey()) ) {
                    if(!dataSnapshot.getKey().equals(((MainActivity) getActivity()).me.getUid()))
                    {
                        liveUser.add(dataSnapshot.getKey());
                    }

                    liveusercount++;
                    setActionBarSubTitle(EToB(String.valueOf(liveusercount)));
                    liveUserListAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                liveUser.remove(dataSnapshot.getKey());
                liveusercount--;
                liveUserListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

        loadinitial();


        chatLiveAdapter = new LiveListAdapter(getActivity(), userArrayLiveList, Fragment_Live.this, dbHelper);


        chatLiveAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = chatLiveAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(chatLiveAdapter);


        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int chatCount = chatLiveAdapter.getItemCount();

                if (lastVisiblePosition < (chatCount - 10) && (lastVisiblePosition != -1)) {
                    jumpToBottom.setVisibility(View.VISIBLE);
                } else {
                    jumpToBottom.setVisibility(View.GONE);

                }
                if (loadCalled > 0) {
                    lastVisiblePosition = lastVisiblePosition + initial;
                    if (lastVisiblePosition < (chatCount - 30) && (lastVisiblePosition != -1)) {
                        LoadMsg.setVisibility(View.VISIBLE);
                    } else {
                        LoadMsg.setVisibility(View.GONE);

                    }
                } else if (loadCalled == 0) {
                    if (lastVisiblePosition < (chatCount - 30) && (lastVisiblePosition != -1)) {
                        LoadMsg.setVisibility(View.VISIBLE);
                    } else {
                        LoadMsg.setVisibility(View.GONE);

                    }
                }

            }
        });


        jumpToBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastPosition =
                        mLinearLayoutManager.getItemCount();
                mMessageRecyclerView.scrollToPosition(lastPosition - 1);

            }
        });
        LoadMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LivePrg.setVisibility(View.VISIBLE);
                eachreduced = 0;
                loadMore();

            }
        });


    }

    private void loadinitial() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).orderByKey().limitToLast(initial).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                if (userArrayLiveList.size() == 0) {
                    receivedKey = dataSnapshot.getKey();
                }

                userArrayLiveList.add(chat);

                chatLiveAdapter.notifyDataSetChanged();
                int lastPosition =
                        mLinearLayoutManager.getItemCount();
                mMessageRecyclerView.scrollToPosition(lastPosition - 1);
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
        LivePrg.setVisibility(View.GONE);
    }

    private void loadMore() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        initial = initial + eachtime * loadCalled;
        loadCalled++;

        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).orderByKey().endAt(receivedKey).limitToLast(eachtime).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (userArrayLiveList.size() == initial) {
                    receivedKey = dataSnapshot.getKey();
                }
                if (eachreduced < 49) {
                    LivePrg.setVisibility(View.GONE);
                    userArrayLiveList.add(eachreduced++, chat);

                }


                chatLiveAdapter.notifyDataSetChanged();
                int lastPosition =
                        mLinearLayoutManager.getItemCount();
                mMessageRecyclerView.scrollToPosition(lastPosition - (lastPosition - 48));

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

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Do something here. This is the event fired when up button is pressed.

                // mFirebaseDatabaseReferenceForLiveCount.child("live_user").child(((MainActivity) getActivity()).me.getUid()).setValue(null);
                // Toast.makeText(getActivity(),"left",Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                onStop();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Constant.Storage.STORAGE_URL).child(Constant.Storage.ATTACHMENT);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendPhotoFirebase(storageRef, selectedImageUri);
                } else {
                    //FIX ME
                }
            }
        }
    }


    private void sendPhotoFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            final long time = System.currentTimeMillis();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");


            try {

                InputStream image_stream = getActivity().getContentResolver().openInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(image_stream);

                int size = (bitmap.getByteCount()) / 1000;

                int imageReductionRate = 100;
                if (size < 2000 && size > 1000) {
                    imageReductionRate = 60;
                } else if (size < 3000 && size > 2000) {
                    imageReductionRate = 50;
                } else if (size < 5000 && size > 3000) {
                    imageReductionRate = 30;
                } else if (size > 5000) {
                    imageReductionRate = 15;
                }


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, imageReductionRate, outputStream);
                byte[] data = outputStream.toByteArray();
                UploadTask uploadTask = imageGalleryRef.putBytes(data);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Log.e(TAG, "onFailure sendPhotoFirebase " + e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        //  Log.i(TAG, "onSuccess sendPhotoFirebase");
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FileModel file = new FileModel("img", downloadUrl.toString(), name, "");
                        Chat chat = new Chat(sender.getName(),
                                sender.getUid(), time, sender.getPhotoUrl(), "Image", file.getUrl_file());
                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        //    chat.setReadStatus(0);
                        //  chat.setMessage("ছবি পাঠিয়েছেন");
                        //dbHelper.addMessage(chat, chat.getChatId());
                        //EventBus.getDefault().post(new ChatSent("yes"));

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred());
                        progressDialog.setMessage("Uploading ..");
                        progressDialog.show();
                    }
                });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            //FIXME
        }

    }
}
