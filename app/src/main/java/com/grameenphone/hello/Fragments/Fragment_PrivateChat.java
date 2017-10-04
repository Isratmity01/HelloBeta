package com.grameenphone.hello.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.grameenphone.hello.Adapter.ChatRoomAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.CircularTransform;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.fcm.FcmNotificationBuilder;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.FileModel;
import com.grameenphone.hello.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.StickerGridView;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;


/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_PrivateChat extends Fragment {
    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    public static String MESSAGES_CHILD = "";
    private static final int PERMISSION_REQUEST_STORAGE = 1044;
    String firstDate, newDate;
    int timefirst, timesecond;
    private RelativeLayout sendbackground;
    int monthfirst, monthsecond;
    public int getMonthfirst() {
        return monthfirst;
    }

    public void setMonthfirst(int monthfirst) {
        this.monthfirst = monthfirst;
    }

    public int getMonthsecond() {
        return monthsecond;
    }

    public void setMonthsecond(int monthsecond) {
        this.monthsecond = monthsecond;
    }

    public int getTimefirst() {
        return timefirst;
    }

    public void setTimefirst(int timefirst) {
        this.timefirst = timefirst;
    }

    public int getTimesecond() {
        return timesecond;
    }

    public void setTimesecond(int timesecond) {
        this.timesecond = timesecond;
    }

    int year;
    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    EmojiconEditText emojiconEditText;
    private ImageView mAddMessageImageView;

    JSONObject chatobject = new JSONObject();

    private User sender;
    private User receiver;

    private User me;

    private String receiverFirebaseToken;


    private DatabaseHelper dbHelper;

    private Button jumpToBottom,LoadMore;

    private View rootView;
    private ImageView emojiImageView;
    SharedPreferences preferences;
    private String[] Monthlist = {"জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"};

    private ProgressDialog progressDialog;


    private static final int IMAGE_GALLERY_REQUEST = 101;
    private ImageView attachment;
    private ImageView pushToTalk;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String receiver_uid,roomName;


    private DatabaseReference mFirebaseDatabaseReference;
    private String room_id;

    SharedPreferences.Editor editor;
    public ChatRoomAdapter chatRoomAdapter;
    public ArrayList<Chat> chats = new ArrayList<Chat>();
    private TextView titletext;
    private ImageView receiverPhoto;
    private int width,height;
    private BroadcastReceiver statusReceiver;
    private IntentFilter mIntent;
    Chat c;
    private float density;
    private static Context context;
    private Bitmap bitmapfinal;
    Calendar calendar;
    Boolean IsSent=false;
    private Toolbar toolbarp2p;
    private static String sChatRoomName = "";
    View fragmentView;
    private int currentPage = 0;
    private static int TOTAL_ITEM_EACH_LOAD = 100;
    public Fragment_PrivateChat() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);


        density = getResources().getDisplayMetrics().density;
        if(density<=1.5)
        {
            width=35;
            height=35;
        }
        else  if(density>1.5 && density<2.5)
        {
            width=35;
            height=35;
        }
        else
        {
            width=60;
            height=60;
        }



        //  ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator ( R.drawable.ic_backiconsmall );
        //  ((AppCompatActivity)getActivity()).getSupportActionBar().setLogo(R.drawable.bell);




        //

        //  setActionBarTitle("নোটিফিকেশন সেটিংস");

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.chat_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    public boolean onSupportNavigateUp(){
        getActivity().getSupportFragmentManager().popBackStack();
        return true;
    }
    public void setActionBarTitle(String title) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("  "+title);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null){


            fragmentView = inflater.inflate(R.layout.activity_chat_room,
                    container, false);
            bindViews(fragmentView);
        }



        return fragmentView;
    }
    private void bindViews(View view) {
        context = getActivity().getApplicationContext();

        emojiconEditText = (EmojiconEditText) view.findViewById(R.id.messageEditText);
        toolbarp2p=(Toolbar)getActivity().findViewById(R.id.toolbar);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.p2pactionbar, null);

        android.support.v7.app.ActionBar ab =  ((AppCompatActivity)getActivity()).getSupportActionBar();
        //toolbarp2p.removeAllViews();
        //toolbarp2p.addView(mCustomView);
        // toolbarp2p.addView(mCustomView,0);
        //titletext=(TextView)toolbarp2p.findViewById(R.id.action_bar_title_1);

        //receiverPhoto=(ImageView)toolbarp2p.findViewById(R.id.conversation_contact_photo);
        // toolbarp2p.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        // titletext.setText(roomName) ;
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbarp2p);

        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jumpToBottom = (Button) view.findViewById(R.id.jump_bottom);
        LoadMore=(Button)view.findViewById(R.id.jump_totop) ;
        mSendButton = (ImageView) view.findViewById(R.id.send_button);
        mSendButton.setBackgroundResource(R.drawable.ic_send);
        sendbackground=(RelativeLayout)view.findViewById(R.id.sendbackground);
        sendbackground.setBackgroundResource(R.drawable.circular_shape);
        sendbackground.setEnabled(false);
        FloatingActionsMenu floatingActionsMenu=  (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        floatingActionsMenu.setVisibility(View.GONE);
        mSendButton.setEnabled(false);
        sendbackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                long time = System.currentTimeMillis();
                final Chat chat = new Chat(sender.getName(), receiver.getName(),
                        sender.getUid(), receiver.getUid(),
                        emojiconEditText.getText().toString(),
                        time, "txt");


                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);

                if(receiver.getFirebaseToken()!=null) {
                    IsSent=true;
                    //  chat.setReadStatus(0);
                    //  dbHelper.addMessage(chat, chat.getChatId());

                }


                emojiconEditText.setText("");
            }
        });
        jumpToBottom.setVisibility(View.GONE);
        rootView = (View) view.findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) view.findViewById(R.id.emoticon);
        emojiButton.setImageResource(R.drawable.emoji);

        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity().getApplicationContext());
        popup.setBackgroundDrawable(null);
        popup.setSizeForSoftKeyboard();
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.emoji);
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = preferences.edit();
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
                if (emojiconEditText == null || emojicon.getEmoji().contains("golu")||emojicon.getEmoji().contains("robot")||emojicon.getEmoji().contains("sticker")||emojicon.getEmoji().contains("jhontu")) {
                    if (emojicon.getEmojiId() == 0) {
                        long time = System.currentTimeMillis();
                        final Chat chat = new Chat(sender.getName(), receiver.getName(),
                                sender.getUid(), receiver.getUid(),
                                String.valueOf(emojicon.getEmoji()),
                                time, "stk");


                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        //chat.setReadStatus(0);
                        //dbHelper.addMessage(chat, chat.getChatId());
                        // EventBus.getDefault().post(new ChatSent("yes"));
                        IsSent=true;
                        return;
                    } else
                        return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if(emojicon.getEmoji().contains("golu")||emojicon.getEmoji().contains("sticker")||emojicon.getEmoji().contains("robot")||emojicon.getEmoji().contains("jhontu"))
                {

                }
                else {
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
        emojiconEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (popup.isShowing()) {
                    popup.dismiss();
                    //   popup.showAtBottom();
                    changeEmojiKeyboardIcon(emojiButton, R.drawable.emoji);
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

        emojiconEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (popup.isShowing()) {
                    popup.dismiss();
                    //   popup.showAtBottom();
                    changeEmojiKeyboardIcon(emojiButton, R.drawable.emoji);
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
        attachment = (ImageView) view.findViewById(R.id.attachment);
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForStoragePermission();
                photoGalleryIntent();
            }
        });





        progressDialog = new ProgressDialog(getActivity());




        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.chatroomRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        room_id =  bundle.getString("room_uid");
        roomName = bundle.getString("room_name");
        dbHelper = new DatabaseHelper(getActivity());
        MESSAGES_CHILD = room_id;
        me = dbHelper.getMe();
        dbHelper.updateNotificationStateOfRoom(MESSAGES_CHILD, 0);
        sender = dbHelper.getMe();
        receiver_uid = (MESSAGES_CHILD.replace(sender.getUid(), "")).replace("_", "");
        receiver = dbHelper.getUser(receiver_uid);
        roomName=receiver.getName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window =getActivity().getWindow();
            Drawable background = getResources().getDrawable(R.drawable.gradient);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbarcolor));
            //window.setBackgroundDrawable(background);
        }
        AppBarLayout appBarLayout=(AppBarLayout)getActivity().findViewById(R.id.appbarmain);


        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

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


        params.width=ViewGroup.LayoutParams.MATCH_PARENT;;
        appBarLayout.setLayoutParams(params);
        //setActionBarTitle(roomName);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.p2pactionbar, null);
        toolbarp2p.addView(mCustomView,0);
        titletext=(TextView)toolbarp2p.findViewById(R.id.action_bar_title_1);
        receiverPhoto=(ImageView) toolbarp2p.findViewById(R.id.conversation_contact_photo);
        receiverPhoto.setVisibility(View.VISIBLE);
        toolbarp2p.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        titletext.setText(roomName) ;

        Glide.with(getActivity()).load(receiver.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(getActivity())).into(receiverPhoto);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbarp2p);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),"Sorry!",Toast.LENGTH_SHORT).show();
        }

        init();
    }

    public void load()
    {
        mFirebaseDatabaseReference.child("chat_rooms").child(MESSAGES_CHILD) .startAt(currentPage*TOTAL_ITEM_EACH_LOAD)
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)

                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            c= dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());
                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                }

                                if (addFlag) {
                                    if (chats.size() == 0) {
                                        calendar.setTimeInMillis(c.getTimestamp());
                                        firstDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                                        firstDate = EToB(firstDate);
                                        setTimefirst(calendar.get(Calendar.DAY_OF_MONTH));
                                        setMonthfirst(calendar.get(Calendar.MONTH));
                                        String Month = Monthlist[getMonthfirst()];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));
                                    }


                                    calendar.setTimeInMillis(c.getTimestamp());
                                    //newDate=String.valueOf(mDay);
                                    setTimesecond(calendar.get(Calendar.DAY_OF_MONTH));
                                    setMonthsecond(calendar.get(Calendar.MONTH));
                                    if (getTimesecond() != getTimefirst()) {
                                        setTimefirst(getTimesecond());

                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String Month = Monthlist[calendar.get(Calendar.MONTH)];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));


                                    } else if (getMonthsecond() > getMonthfirst() && getTimesecond() == getTimefirst()) {
                                        String Month = Monthlist[getMonthsecond()];
                                        setMonthfirst(getMonthsecond());
                                        setTimefirst(getTimesecond());
                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));

                                    }
                                    chats.add(c);
                                }

                                if(IsSent)
                                {   IsSent=false;
                                    //dbHelper.addMessage(c,c.getChatId());
                                    c.setReadStatus(0);



                                    try {
                                        chatobject=populateJsonChat(chatobject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    sendPushNotificationToReceiver( chatobject, me.getFirebaseToken(), receiverFirebaseToken, MESSAGES_CHILD);




                                }


                            }
                            /*if(c.getReceiverUid().equals(me.getUid()))
                            {
                              c.setReadStatus(1);
                                dbHelper.addMessage(c,c.getChatId());
                            }*/
                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);


                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            c = dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());
                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                    if(data.getReadStatus() != c.getReadStatus()){
                                        data.setReadStatus(c.getReadStatus());
                                        chatRoomAdapter.notifyDataSetChanged();
                                        int lastPosition =
                                                mLinearLayoutManager.getItemCount();
                                        mMessageRecyclerView.scrollToPosition(lastPosition - 1);
                                    }

                                }

                                if (addFlag) {
                                    chats.add(c);
                                }


                            }

                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);

                        }


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


    public void loadinitial()
    {
        mFirebaseDatabaseReference.child("chat_rooms").child(MESSAGES_CHILD)


                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            c= dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());

                                dbHelper.addMessage( MESSAGES_CHILD, c, c.getChatId(), c.getReadStatus() );

                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                }

                                if (addFlag) {
                                    if (chats.size() == 0) {
                                        calendar.setTimeInMillis(c.getTimestamp());
                                        firstDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                                        firstDate = EToB(firstDate);
                                        setTimefirst(calendar.get(Calendar.DAY_OF_MONTH));
                                        setMonthfirst(calendar.get(Calendar.MONTH));
                                        String Month = Monthlist[getMonthfirst()];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));
                                    }


                                    calendar.setTimeInMillis(c.getTimestamp());
                                    //newDate=String.valueOf(mDay);
                                    setTimesecond(calendar.get(Calendar.DAY_OF_MONTH));
                                    setMonthsecond(calendar.get(Calendar.MONTH));
                                    if (getTimesecond() != getTimefirst()) {
                                        setTimefirst(getTimesecond());

                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String Month = Monthlist[calendar.get(Calendar.MONTH)];
                                        year = calendar.get(Calendar.YEAR);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));


                                    } else if (getMonthsecond() > getMonthfirst() && getTimesecond() == getTimefirst()) {
                                        String Month = Monthlist[getMonthsecond()];
                                        setMonthfirst(getMonthsecond());
                                        setTimefirst(getTimesecond());
                                        firstDate = String.valueOf(getTimefirst());
                                        firstDate = EToB(firstDate);
                                        String YEAR = EToB(String.valueOf(year));
                                        chats.add(new Chat(dataSnapshot.getKey(), sender.getName(), receiver.getName(),
                                                sender.getUid(), receiver.getUid(),
                                                Month + " " + firstDate + ", " + YEAR,
                                                (long) 1, ""));

                                    }
                                    chats.add(c);
                                }

                                if(IsSent)
                                {   IsSent=false;
                                    //dbHelper.addMessage(c,c.getChatId());
                                    c.setReadStatus(0);

                                    try {
                                        chatobject=populateJsonChat(chatobject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    sendPushNotificationToReceiver( chatobject, me.getFirebaseToken(), receiverFirebaseToken, MESSAGES_CHILD);



                                }


                            }
                            /*if(c.getReceiverUid().equals(me.getUid()))
                            {
                              c.setReadStatus(1);
                                dbHelper.addMessage(c,c.getChatId());
                            }*/
                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);


                        }


                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                        if (dataSnapshot.hasChildren()) {

                            c = dataSnapshot.getValue(Chat.class);
                            if (c != null) {
                                c.setChatId(dataSnapshot.getKey());
                                boolean addFlag = true;
                                for (Chat data : chats) {
                                    if (data.getChatId().equals(c.getChatId())) {
                                        addFlag = false;
                                    }

                                    if(data.getReadStatus() != c.getReadStatus()){
                                        data.setReadStatus(c.getReadStatus());
                                        chatRoomAdapter.notifyDataSetChanged();
                                        int lastPosition =
                                                mLinearLayoutManager.getItemCount();
                                        mMessageRecyclerView.scrollToPosition(lastPosition - 1);
                                    }

                                }

                                if (addFlag) {
                                    chats.add(c);
                                }


                            }

                            chatRoomAdapter.notifyDataSetChanged();
                            int lastPosition =
                                    mLinearLayoutManager.getItemCount();
                            mMessageRecyclerView.scrollToPosition(lastPosition - 1);

                        }


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
    public void init()
    {

        receiverFirebaseToken = receiver.getFirebaseToken();
        sChatRoomName = receiver.getName();
        calendar = Calendar.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        //chats = dbHelper.getAllMsg(sender.getUid(),receiver.getUid());
        //System.out.println(chats.size() + " Here is the size ");

        chatRoomAdapter = new ChatRoomAdapter(getActivity().getApplicationContext(), chats, sender, receiver, MESSAGES_CHILD);

        chatRoomAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = chatRoomAdapter.getItemCount();
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


        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(chatRoomAdapter);

        loadinitial();
        //  Toast.makeText(getActivity(),"asd " +chats.size(),Toast.LENGTH_SHORT).show();

        mMessageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int chatCount = chatRoomAdapter.getItemCount();

                if (lastVisiblePosition < (chatCount - 10) && (lastVisiblePosition != -1)) {
                    jumpToBottom.setVisibility(View.VISIBLE);
                } else {
                    jumpToBottom.setVisibility(View.GONE);

                }
            }
        });
        LoadMore.setVisibility(View.GONE);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TOTAL_ITEM_EACH_LOAD=TOTAL_ITEM_EACH_LOAD+TOTAL_ITEM_EACH_LOAD;
                chats.clear();
                loadinitial();
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


        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {

                    mSendButton.setVisibility(View.VISIBLE);
                    sendbackground.setBackgroundResource(R.drawable.circular_shape);
                    sendbackground.setEnabled(true);
                    mSendButton.setEnabled(true);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getActivity().getApplicationContext(),R.color.white)  );



                } else {
                    sendbackground.setBackgroundResource(R.drawable.circular_shape);
                    sendbackground.setEnabled(false);
                    mSendButton.setVisibility(View.VISIBLE);
                    mSendButton.setEnabled(false);
                    mSendButton.setColorFilter(  ContextCompat.getColor(getActivity().getApplicationContext(),R.color.white)  );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.chat_info:
                Bundle bundle = new Bundle();
                bundle.putString("room_uid", room_id);
                bundle.putString("room_name", roomName);

                Fragment_ChatProfile fragmentchatprofile = new Fragment_ChatProfile();

                fragmentchatprofile.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragmentchatprofile);
                fragmentTransaction.addToBackStack("p2pinfo");
                fragmentTransaction.commit();
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void setActionBarSubTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
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




    private JSONObject populateJsonChat (JSONObject chat) throws JSONException {
        chat.put("chatId", c.getChatId());
        chat.put("receiver", c.getReceiver());
        chat.put("receiverUid",c.getReceiverUid());
        chat.put("sender", c.getSender());
        chat.put("messageType", c.getMessageType());
        chat.put("senderUid",c.getSenderUid());
        chat.put("timestamp", c.getTimestamp());
        chat.put("photoUrl", c.getPhotoUrl());
        chat.put("message",c.getMessage());
        chat.put("readStatus",c.getReadStatus());

        if(c.getFile()!=null)

        {
            chat.put("type",c.getFile().getType());
            chat.put("url_file", c.getFile().getUrl_file());
            chat.put("name_file",c.getFile().getName_file());
            chat.put("size_file",c.getFile().getSize_file());
        }

        return chat;
    }





    private void sendPushNotificationToReceiver(JSONObject chat,
                                                String firebaseToken,
                                                String receiverFirebaseToken,
                                                String roomId) {
        //   EventBus.getDefault().post(new ChatSent("yes"));
        FcmNotificationBuilder.initialize()
                .notificationType("p2p")
                .setReceived(chat)
                .roomUid(roomId)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }





    public static String getChatRoomName() {
        return sChatRoomName;
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
                        Chat chat = new Chat(sender.getName(), receiver.getName(),
                                sender.getUid(), receiver.getUid(), sender.getPhotoUrl(), "Image", time, file, file.getType());
                        mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD).child(MESSAGES_CHILD).push().setValue(chat);
                        //    chat.setReadStatus(0);
                        //  chat.setMessage("ছবি পাঠিয়েছেন");
                        //dbHelper.addMessage(chat, chat.getChatId());
                        //EventBus.getDefault().post(new ChatSent("yes"));
                        IsSent=true;
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




    public void askForStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission( getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                    builder.setTitle("Storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Storage access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST_STORAGE);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions( getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                photoGalleryIntent();
            }
        } else {
            photoGalleryIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photoGalleryIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(), "No Permissions ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




}