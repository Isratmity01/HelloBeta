package com.grameenphone.hello.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.grameenphone.hello.Fragments.Fragment_Live;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.Utils.DateTimeUtility;
import com.grameenphone.hello.Utils.ImageDialog;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.User;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.grameenphone.hello.Utils.Userlevels.getLevelName;


public class LeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String path;
    File  wallpaperDirectory;
    private Uri imageUri;
    private String filename;
    private  File finalfile;
    private String FilePath="https://firebasestorage.googleapis.com/v0/b/mars-e7047.appspot.com/o/";
    private Context context;
    private int se;
    private ArrayList<User> chatArrayList = new ArrayList<User>();
    private Bitmap bitmapfinal;
    private LayoutInflater inflater;
    private Fragment_Live fragment_live;
    private DatabaseHelper databaseHelper;
    public LeaderAdapter(Context context, ArrayList<User> rooms,DatabaseHelper databaseHelper1) {
        this.context = context;
        this.chatArrayList = rooms;
        this.databaseHelper=databaseHelper1;


    }

    public void clear() {
        int size = chatArrayList.size();
        chatArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_leaderboardcontent, parent, false);
        final MessageViewHolder holder = new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final MessageViewHolder viewHolder = (MessageViewHolder) holder;
        final User user = chatArrayList.get(position);
        se=position++;
        viewHolder.title.setText(user.getName());
        viewHolder.serials.setText(EToB(String.valueOf(se)));
        String level=getLevelName(user.getUserpoint());
        viewHolder.leveltext.setText(level);
        if(level.equals("লেভেল ১")){
           viewHolder.badgedesign.setBackgroundResource(R.drawable.level_1);
        }
        if(level.equals("লেভেল ২")){
            viewHolder.badgedesign.setBackgroundResource(R.drawable.level_2);
        }
        if(level.equals("লেভেল ৩")){
            viewHolder.badgedesign.setBackgroundResource(R.drawable.level_3);
        }
        if(level.equals("লেভেল ৪")){
            viewHolder.badgedesign.setBackgroundResource(R.drawable.level_4);
        }  if(level.equals("লেভেল ৫")){
            viewHolder.badgedesign.setBackgroundResource(R.drawable.level_5);
        }

        Glide.with(  viewHolder.userimage.getContext()).load( user.getPhotoUrl() ).bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.drawable.hello1)
                .into(  viewHolder.userimage);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            //Start new list activity

            public void onClick(View v) {

            }
        });



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
    public int getItemCount() {
        return chatArrayList.size();
    }


    //return the filter class object

    public int getImage(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }
    private class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView leveltext;
        public TextView serials;
        public ImageView badgedesign;
        public ImageView userimage;



        public ImageView badge;
        public MessageViewHolder(View v) {
            super(v);
            badge=(ImageView)itemView.findViewById(R.id.badge);
            leveltext = (TextView) itemView.findViewById(R.id.leveltext);
            title = (TextView) itemView.findViewById(R.id.nameView);
            serials = (TextView) itemView.findViewById(R.id.serial);
            badgedesign = (ImageView) itemView.findViewById(R.id.badges);
            userimage = (ImageView) itemView.findViewById(R.id.ImageView);

        }
    }


    public void refresh() {
        //manipulate list
        notifyDataSetChanged();

    }

    public int refreshfromswipe() {
        //manipulate list
        notifyDataSetChanged();
        return 1;
    }

}
