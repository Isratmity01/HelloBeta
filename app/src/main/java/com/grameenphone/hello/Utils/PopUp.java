package com.grameenphone.hello.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.grameenphone.hello.Adapter.LeaderAdapter;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by HP on 7/7/2017.
 */

public class PopUp extends AppCompatActivity {
    File direct;
    ProgressDialog progressBar;

    private static final int PERMISSION_REQUEST_STORAGE = 1034;
    Bitmap bitmapreceived;
    private ImageView mDialog;
    String  fromwhere;
    RecyclerView leaders;
    private LeaderAdapter leaderAdapter;
    private ArrayList<User>all=new ArrayList<>();
    private ArrayList<User>topten=new ArrayList<>();
    private ProgressBar progressBar1;
    private Toolbar toolbar;
    String root_sd = Environment.getExternalStorageDirectory().toString();
    // Set the URL to download image
    String PhotoPictureDownLoadPath;
   private Bitmap bitmap;
    private Context mcontext;
    private String yesOrNo;
    private Button close,close2;
    private DatabaseHelper databaseHelper;
    File wallpaperDirectory;
    ArrayList<String> toBeScanned = new ArrayList<String>();
    public static void start(Context context,String fromwhere) {
        Intent intent = new Intent(context, PopUp.class);
        intent.putExtra("From", fromwhere);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
public PopUp()
{

}

    public PopUp(Context context){

        this.mcontext = context;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        databaseHelper=new DatabaseHelper(PopUp.this);
        fromwhere = intent.getStringExtra("From");
        if(fromwhere.equals("point"))
        {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.item_pointrules);
            close=(Button)findViewById(R.id.closethis);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        else {
            setContentView(R.layout.popup_dialog);
            leaders=(RecyclerView)findViewById(R.id.leaderboardRecyclerView);


            all=databaseHelper.getTopten();
            for(int i=0;i<10;i++)
            {
                topten.add(i,all.get(i));
            }
            leaderAdapter = new LeaderAdapter(this, topten,databaseHelper);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setAutoMeasureEnabled(true);
            leaders.setNestedScrollingEnabled(false);
            leaders.setLayoutManager(layoutManager);
            leaders.setAdapter(leaderAdapter);
            close2=(Button)findViewById(R.id.closethiss);
            close2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            // Glide.with(this).load(photoUrl).into(mDialog);

            //finish the activity (dismiss the image dialog) if the user clicks
            //anywhere on the image

        }
    }







}
