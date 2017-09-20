package com.grameenphone.hello.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.Activities.ProfileEditActivity;
import com.grameenphone.hello.Adapter.AvatarAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.CircularTransform;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.Utils.FileUtil;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.app.Activity.RESULT_OK;

/**
 * Created by shadman.rahman on 13-Jun-17.
 */

public class Fragment_UserProfileEdit extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String photo;
    StorageReference storageRef;
     User user;
    public Integer [] avatars = {
            R.drawable.avatar_balloon,
            R.drawable.avatar_cycle, R.drawable.avatar_forest,
            R.drawable.avatar_fountain,
            R.drawable.avatar_airplane,
            R.drawable.avatar_mountains,  R.drawable.avatar_house,  R.drawable.avatar_river,
            R.drawable.avatar_suitecase,R.drawable.avatar_windmill};
    AvatarAdapter avatarAdapter;
    View fragmentView;


    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private int randomnumber;
    private ImageView gallery;
    private CircleImageView userPic;
    private GridView gridView;
    private String username, photourl;
    private EditText name;
    private static final int IMAGE_GALLERY_REQUEST = 101;
    private String profilepicUrl = null;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    public static final String USERS_CHILD = "users";
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final String STORAGE_URL = "gs://mars-e7047.appspot.com";
    public static final String ATTACHMENT = "attachments";

    private Boolean gridClicked=false;


    public Fragment_UserProfileEdit() {
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
        Bundle bundle = this.getArguments();
        username = bundle.getString("name");
        photourl = bundle.getString("photoUrl");



        setActionBarTitle("প্রোফাইল এডিট করুন");

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.group_menu, menu);
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


            fragmentView = inflater.inflate(R.layout.fragment_profile_edit,
                    container, false);
            bindViews(fragmentView);
        }


        return fragmentView;
    }
    private void bindViews(View view) {
        gridView=(GridView)view.findViewById(R.id.gridView);
        userPic=(CircleImageView) view.findViewById(R.id.user_pic);
        name = (EditText) view.findViewById(R.id.name_field);
        progressDialog=new ProgressDialog(getActivity());
        name.setText(username);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mFirebaseUser = mAuth.getCurrentUser();
        }
        gallery=(ImageView)view.findViewById(R.id.gallary_upload);
        Glide.with(getActivity()).load(photourl)
                .into(userPic);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                gridClicked=true;

                // TODO Auto-generated method stub
                Glide.with(getActivity())
                        .load(avatars[position])
                        .asBitmap().transform(new CircularTransform(getActivity()))
                        .into(new SimpleTarget<Bitmap>(200,200) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                bitmap=resource;
                                userPic.setImageBitmap(resource);
                            }
                        });
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked=false;
                photoGalleryIntent();
            }
        });
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked=false;
                photoGalleryIntent();
            }
        });
        FloatingActionsMenu floatingActionsMenu=  (FloatingActionsMenu) getActivity().findViewById(R.id.multiple_actions);
        floatingActionsMenu.setVisibility(View.GONE);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        storageRef = storage.getReferenceFromUrl(STORAGE_URL).child(ATTACHMENT);
        init();
    }
    public void init()
    {
        populateList();
    }
    public void populateList()
    { // randomnumber=Randomnumber();

        avatarAdapter=new AvatarAdapter(getActivity(),avatars);
        gridView.setAdapter(avatarAdapter);
       // gridView.setItemChecked(randomnumber,true);

       //

    }
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }
    public int Randomnumber()
    {
        Random generator = new Random();
        int randomIndex = generator.nextInt(avatars.length);
        return randomIndex;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {

                    try {
                        File actualImage = FileUtil.from(getActivity(), data.getData());

                        Bitmap bitmap = new Compressor(getActivity())
                                .setMaxWidth(191)
                                .setMaxHeight(191)
                                .setQuality(75).compressToBitmap(actualImage);

                        sendPhotoFirebase(storageRef, bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        InputStream is = getActivity().getContentResolver().openInputStream(selectedImageUri);

                        Drawable d = Drawable.createFromStream(is, selectedImageUri.toString());
                       /* Glide.with(getActivity()).
                                load("").placeholder(d).bitmapTransform(new CropCircleTransformation(getActivity()))
                                .into(userPic);*/
                        userPic.setImageDrawable(d);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    //FIX ME
                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // Do onliTck on menu action here

                getActivity().onBackPressed();
                return true;
            case R.id.action_save:
                // Do onliTck on menu action here
                if(gridClicked){
                    sendPhotoFirebase(storageRef,bitmap);
                }
                else
                saveInfo();

                return true;
        }
        return false;
    }

    public void saveInfo() {
        String user_name = name.getText().toString();

        if ((!user_name.isEmpty() && photourl != null) || (!user_name.isEmpty() && profilepicUrl != null)) {


            String phone = mFirebaseUser.getPhoneNumber();


            if (profilepicUrl != null) {
                    photo = profilepicUrl;
                } else photo = photourl;



            String uid = mFirebaseUser.getUid();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LoginName", uid);
            editor.apply();

           user = new User(uid, user_name, phone, photo);

            user.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());


            boolean success = false;
            try {
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabaseReference.child(USERS_CHILD).child(mFirebaseUser.getUid())
                        .setValue(user);


                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                dbHelper.addMe(user);


                success = true;
            } catch (Error ignored) {

            }

            if (success) {
                Toast.makeText(getActivity(),"ইনফরমেশন আপডেট হয়েছে",Toast.LENGTH_SHORT).show();
              getActivity().onBackPressed();
            }


        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("");
            if (user_name.isEmpty()) {
                alert.setMessage("দয়া করে আপনার নাম লিখুন");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
            } else {
                alert.setMessage("দয়া করে ছবি আপলোড করুন");
                alert.setPositiveButton("ঠিক আছে", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        photoGalleryIntent();
                    }
                });
            }

            alert.show();
        }

    }
    private void sendPhotoFirebase(StorageReference storageReference, Bitmap bitmap) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            final long time = System.currentTimeMillis();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.setHasAlpha(true);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] data = outputStream.toByteArray();



            UploadTask uploadTask = imageGalleryRef.putBytes(data);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    profilepicUrl = taskSnapshot.getDownloadUrl().toString();
                    if(gridClicked)saveInfo();


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred());
                    progressDialog.setMessage("Uploading ..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }
            });


        } else {
            //FIXME
        }

    }


}
