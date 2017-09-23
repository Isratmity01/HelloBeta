package com.grameenphone.hello.Fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.hello.Adapter.ContactListRecyclerAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.SelectUser;
import com.grameenphone.hello.model.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Fragment_Contacts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    Cursor phones, email;
    // ArrayList
    ArrayList<SelectUser> selectUsers = new ArrayList<SelectUser>();
    ArrayList<SelectUser> distinctselectUsers = new ArrayList<SelectUser>();
    ArrayList<User> helloUsers = new ArrayList<>();
    List<SelectUser> temp;


    private Toolbar toolbar;
    List<String>hellophones=new ArrayList<>();;
  
    View fragmentView;
    // Pop up
    ContentResolver resolver;

    private User me;
    FragmentManager fragmentManager;

    private ContactListRecyclerAdapter contactListRecyclerAdapter;
    private ProgressBar contactsLoader;
    private RecyclerView listView;
    private DatabaseHelper databaseHelper;

    private DatabaseReference mFirebaseDatabaseReferenceForRequest;


    public Fragment_Contacts() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper( getActivity().getApplicationContext() );
        me = databaseHelper.getMe();

        mFirebaseDatabaseReferenceForRequest = FirebaseDatabase.getInstance().getReference();

        setActionBarTitle("নতুন ম্যাসেজ");
    }

    public void setActionBarTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (fragmentView == null) {


            fragmentView = inflater.inflate(R.layout.fragment_blank2,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here

                getActivity().onBackPressed();
                return true;

        }
        return false;
    }

    private void bindViews(View view) {

        toolbar=(Toolbar)getActivity().findViewById(R.id.toolbar);

        databaseHelper=new DatabaseHelper(getActivity());
        contactsLoader=(ProgressBar)view.findViewById(R.id.contactsloader);
        FloatingActionsMenu floatingActionsMenu=  (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
      
        floatingActionsMenu.setVisibility(View.GONE);
        listView = (RecyclerView) view.findViewById(R.id.contacts_list);
        MatrixCursor mc = new MatrixCursor(new String[]{
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        });
        hellophones = databaseHelper.getAllnumber();

        try {
            resolver = getActivity().getApplicationContext().getContentResolver();
            phones = getActivity().getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (phones.getCount() > 0) {
                selectUsers.clear();
                LoadContact loadContact = new LoadContact();
                loadContact.execute();
            } else
                Toast.makeText(getActivity().getApplicationContext(), "আপনার মোবাইলে কোন কন্টাক্ট সেভ করা নেই", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.toString()}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "contact permission is granted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.p2pactionbar, null);
        toolbar.addView(mCustomView,0);
        TextView titleText = (TextView) toolbar.findViewById(R.id.action_bar_title_1);
        ImageView receiver = (ImageView) toolbar.findViewById(R.id.conversation_contact_photo);
        receiver.setVisibility(View.GONE);
        titleText.setText("নতুন ম্যাসেজ");
        init();
    }

    public void init() {

    }

    public void openDialogue(final User user, int reqStatus) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.req_layout, null);
        TextView name = (TextView) view.findViewById(R.id.profile_name);
        name.setText(user.getName());
        ImageView profile = (ImageView) view.findViewById(R.id.profile_picture);
        Glide.with(getActivity()).load(user.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(getActivity()))
                .placeholder(R.drawable.hello1)
                .into(profile);
        alertadd.setView(view);


        if (reqStatus == 1) {

            final String chatRoomId = Compare.getRoomName(user.getUid(), me.getUid());
            final ChatRoom chatRoom = databaseHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("ম্যাসেজ পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartP2p(chatRoomId, chatRoom.getName());
                    dialog.dismiss();
                }
            });


        } else if (reqStatus == 2) {

            final String chatRoomId = Compare.getRoomName(user.getUid(), me.getUid());
            final ChatRoom chatRoom = databaseHelper.getRoom(chatRoomId);

            alertadd.setPositiveButton("ম্যাসেজ রিকুয়েস্ট এক্সেপ্ট করুন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chatRoom.setRequestStatus(1);

                    mFirebaseDatabaseReferenceForRequest.child("users_chat_room")
                            .child(me.getUid())
                            .child(chatRoomId)
                            .setValue(chatRoom);


                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(me.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(me.getPhotoUrl());
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

            alertadd.setPositiveButton("ম্যাসেজ রিকুয়েস্ট পাঠিয়েছেন", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


        } else {

            alertadd.setPositiveButton("ম্যাসেজ রিকুয়েস্ট পাঠান", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String chatRoomId = Compare.getRoomName(user.getUid(), me.getUid());
                    final ChatRoom chatRoomForSender = new ChatRoom();

                    chatRoomForSender.setName(me.getName());
                    chatRoomForSender.setRoomId(chatRoomId);
                    chatRoomForSender.setPhotoUrl(me.getPhotoUrl());
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
                            .child(me.getUid())
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










    @Override
    public void onStop() {
        super.onStop();
        phones.close();
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone


            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                    return null;
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    if (phoneNumber != null) {
                        phoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                        if (phoneNumber.length() >= 10) {
                            String SplittedphoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
                            if (hellophones.contains(SplittedphoneNumber))

                            {
                                SelectUser selectUser = new SelectUser();

                                User user = databaseHelper.getUserbynumber(SplittedphoneNumber);
                                selectUser.setName(user.getName());
                                selectUser.setThumb(user.getPhotoUrl());
                                selectUser.setPhone(user.getPhone());
                                selectUser.setUid(user.getUid());
                                selectUser.setHelloUser(true);
                                selectUser.setEmail(id);
                                selectUsers.add(selectUser);
                            }
                        }
                    }


                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        private void removeDuplicates(ArrayList<SelectUser> list) {
            int count = list.size();

            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    if (list.get(i).getPhone().equals(list.get(j).getPhone())) {
                        list.remove(j--);
                        count--;
                    }
                }
            }
        }

        private void combineHelloUsers(ArrayList<SelectUser> selectUsers) {
            int size_helloUserss = helloUsers.size();
            int size_selectUsers = selectUsers.size();
            if (size_helloUserss <= size_selectUsers) {
                for (int i = 0; i < size_helloUserss; i++) {

                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // HashSet<SelectUser> hashSet = new HashSet<SelectUser>();
            //hashSet.addAll(selectUsers);
            //selectUsers.clear();
            //selectUsers.addAll(hashSet);
            //   Collections.sort(selectUsers);
            removeDuplicates(selectUsers);

            listView.setLayoutManager(new LinearLayoutManager(getActivity()));
            contactListRecyclerAdapter = new ContactListRecyclerAdapter(selectUsers, getActivity(), Fragment_Contacts.this);
            listView.setAdapter(contactListRecyclerAdapter);
            contactsLoader.setVisibility(View.GONE);
            // Select item on listclick


        }
    }

}
