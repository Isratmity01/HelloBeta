package com.grameenphone.hello.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.grameenphone.hello.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by HP on 7/7/2017.
 */

public class ImageDialog extends AppCompatActivity {
    File direct;
    ProgressDialog progressBar;

    private static final int PERMISSION_REQUEST_STORAGE = 1034;
    Bitmap bitmapreceived;
    private ImageView mDialog;
    String  photoUrl;
    private ProgressBar progressBar1;
    private Toolbar toolbar;
    String root_sd = Environment.getExternalStorageDirectory().toString();
    // Set the URL to download image
    String PhotoPictureDownLoadPath;
   private Bitmap bitmap;
    private Context mcontext;
    private String yesOrNo;
    File wallpaperDirectory;
    ArrayList<String> toBeScanned = new ArrayList<String>();
    public static void start(Context context, String photoUrl,String text) {
        Intent intent = new Intent(context, ImageDialog.class);
        intent.putExtra("photoUrl", photoUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("fromp2p", text);
        context.startActivity(intent);
    }
public ImageDialog()
{

}

    public ImageDialog(Context context){

        this.mcontext = context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar1=(ProgressBar)findViewById(R.id.imageloaderind);
        setSupportActionBar(toolbar);
         android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Drawable upArrow = ContextCompat.getDrawable(ImageDialog.this,R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ab.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
mcontext=ImageDialog.this;
        photoUrl = intent.getStringExtra("photoUrl");
        yesOrNo = intent.getStringExtra("fromp2p");

        PhotoPictureDownLoadPath=photoUrl;

        mDialog = (ImageView)findViewById(R.id.your_image);

        Glide.with(this)
                .load(photoUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(300,300) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        progressBar1.setVisibility(View.GONE);
                        bitmap=resource;
                        if(yesOrNo.equals("yes"))createImageFromBitmap(bitmap,photoUrl);
                        mDialog.setImageBitmap(resource);
                    }
                });

    // Glide.with(this).load(photoUrl).into(mDialog);

        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image


    }
    public void createImageFromBitmap(Bitmap bmp,String Url) {

        FileOutputStream fileOutputStream = null;
        try {

            // create a File object for the parent directory
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/Hello/HelloImages");
            // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();

            Url=Url.substring(Url.lastIndexOf("/") + 1);
            String fname = Url+ ".png";
            File file = new File(wallpaperDirectory, fname);
            System.out.println(file.getAbsolutePath());
            if (!file.exists())
            {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                      Toast.makeText(mcontext,"ইমেজ সেভ হয়েছে",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaScannerConnection.scanFile(mcontext, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
            }
            else {
                Toast.makeText(mcontext, "অলরেডি সেভ করা আছে", Toast.LENGTH_SHORT).show();
            }

        } finally {

        }
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_image, menu);
        if(yesOrNo.equals("yes"))menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save_this_image:

                askForStoragePermission();
             return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void askForStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Storage access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Storage access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST_STORAGE);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                createImageFromBitmap(bitmap,photoUrl);
            }
        }
        else{
            createImageFromBitmap(bitmap,photoUrl);
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
                    createImageFromBitmap(bitmap,photoUrl);
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


}
