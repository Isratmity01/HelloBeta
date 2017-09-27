package com.grameenphone.hello.Activities;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.grameenphone.hello.Adapter.AvatarAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.CircularTransform;
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


public class ProfileEditActivity extends AppCompatActivity {

    private CircleImageView profilePic;
    private EditText name;
    private Button done;
    String photo;
    private String profilepicUrl = null;


    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static final String STORAGE_URL = "gs://mars-e7047.appspot.com";
    public static final String ATTACHMENT = "attachments";
    public Integer [] avatars = {R.drawable.asian_girl,
            R.drawable.batman,
            R.drawable.baymax, R.drawable.chicken,
            R.drawable.girl_in_glasses,
            R.drawable.minion,
            R.drawable.pringles,
            R.drawable.robocop,
            R.drawable.supergirl,R.drawable.superman,R.drawable.wolverine,R.drawable.wonderwoman};
    AvatarAdapter avatarAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String photoUrl, Name;
    private int randomnumber;
    TextView NameTitle, BarTitle;
    private ImageView gallery;
    private ImageButton Back, Save;
    public static final String USERS_CHILD = "users";
    private DatabaseReference mFirebaseDatabaseReference;
    private GridView gridView;
    private ProgressDialog progressDialog;
    private static final int IMAGE_GALLERY_REQUEST = 101;
    private Bitmap bitmap;
    private Boolean gridClicked=false;

    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.fragment_profile_edit);


        Intent intent = getIntent();
        photoUrl = intent.getStringExtra("photoUrl");
        Name = intent.getStringExtra("name");

        storageRef = storage.getReferenceFromUrl(STORAGE_URL).child(ATTACHMENT);
        profilePic = (CircleImageView) findViewById(R.id.user_pic);
        gallery=(ImageView)findViewById(R.id.gallary_upload);

        gridView=(GridView)findViewById(R.id.gridView);

        name = (EditText) findViewById(R.id.name_field);
        NameTitle = (TextView) findViewById(R.id.name_field_title);
        NameTitle.setText("আপনার নাম লিখুন");
        populateList();
        BarTitle = (TextView) findViewById(R.id.bartitle);
        done = (Button) findViewById(R.id.profile_done);
        done.setVisibility(View.VISIBLE);
        done.setBackgroundResource(R.drawable.profile_button_shape);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mFirebaseUser = mAuth.getCurrentUser();
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked=false;
                photoGalleryIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridClicked=false;
                photoGalleryIntent();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gridClicked){
                    sendPhotoFirebase(storageRef,bitmap);
                }
                else
                    saveInfo();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // TODO Auto-generated method stub
                Glide.with(ProfileEditActivity.this).load(avatars[position])
                        .into(profilePic);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                gridClicked=true;

                avatarAdapter.setSelected_position(position);
                // TODO Auto-generated method stub
                Glide.with(ProfileEditActivity.this)
                        .load(avatars[position])
                        .asBitmap().transform(new CircularTransform(ProfileEditActivity.this))
                        .into(new SimpleTarget<Bitmap>(200,200) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                bitmap=resource;
                                profilePic.setImageBitmap(resource);
                            }
                        });
            }
        });
    }
    public void populateList()
    {  randomnumber=Randomnumber();

        avatarAdapter=new AvatarAdapter(ProfileEditActivity.this,avatars,randomnumber);
        gridClicked=true;
        Glide.with(ProfileEditActivity.this)
                .load(avatars[randomnumber])
                .asBitmap().transform(new CircularTransform(ProfileEditActivity.this))
                .into(new SimpleTarget<Bitmap>(200,200) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        bitmap=resource;
                        profilePic.setImageBitmap(resource);
                    }
                });
        gridView.setAdapter(avatarAdapter);
        // gridView.setItemChecked(randomnumber,true);

        //

    }
    public void saveInfo() {
        String user_name = name.getText().toString();

        if ((!user_name.isEmpty() && photoUrl != null) || (!user_name.isEmpty() && profilepicUrl != null)) {


            String phone = mFirebaseUser.getPhoneNumber();
            if (profilepicUrl != null) {
                photo = profilepicUrl;
            } else photo = photoUrl;

            String uid = mFirebaseUser.getUid();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileEditActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LoginName", uid);
            editor.apply();

            final User user = new User(uid, user_name, phone, photo);

            user.setFirebaseToken(FirebaseInstanceId.getInstance().getToken());


            boolean success = false;
            try {
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabaseReference.child(USERS_CHILD).child(mFirebaseUser.getUid())
                        .setValue(user);


                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.addMe(user);


                success = true;
            } catch (Error ignored) {

            }

            if (success) {
                Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }


        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ProfileEditActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_URL).child(ATTACHMENT);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {

                    try {
                        File actualImage = FileUtil.from(this, data.getData());

                        Bitmap bitmap = new Compressor(this)
                                .setMaxWidth(191)
                                .setMaxHeight(191)
                                .setQuality(75).compressToBitmap(actualImage);

                        sendPhotoFirebase(storageRef, bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    try {
                        InputStream is = getContentResolver().openInputStream(selectedImageUri);

                        Drawable d = Drawable.createFromStream(is, selectedImageUri.toString());
                        profilePic.setImageDrawable(d);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    //FIX ME
                }
            }
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

    public int Randomnumber()
    {
        Random generator = new Random();
        int randomIndex = generator.nextInt(avatars.length);
        return randomIndex;
    }
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }


}
