package com.grameenphone.hello.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.grameenphone.hello.Fragments.Fragment_Live;
import com.grameenphone.hello.R;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.User;


import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class LiveUserListAdapterInside extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> users;
    private LayoutInflater inflater;
    private RequestManager glideRequest;
    private Fragment_Live fragment_live;
    private DatabaseHelper dbhelper;
    private final static int FADE_DURATION = 1000 ;// in milliseconds
    public LiveUserListAdapterInside(Context context, ArrayList<String> users, DatabaseHelper databaseHelper,Fragment_Live fragmentLive){
        this.context = context;
        this.users = users;
        this.dbhelper=databaseHelper;
        this.fragment_live=fragmentLive;

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_live_screen, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;
        final User current = dbhelper.getUser( users.get(position) );
       // final ChatRoom gainedChatroom=dbhelper.getRoombyName(current.getName());
        if(current != null) {
            if(current.getPhotoUrl() != null)
            {

                Glide.with(itemHolder.friendImageView.getContext()).load( current.getPhotoUrl() ).bitmapTransform(new CropCircleTransformation(context))
                        .placeholder(R.drawable.hello1)
                        .into(itemHolder.friendImageView);
            }
            String lilname=current.getName().trim().split("\\s+")[0];
            itemHolder.text.setText(lilname);

        }

        setFadeAnimation(itemHolder.friendImageView);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                (fragment_live).openDialogue(current);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {

    private ImageView friendImageView;
        private TextView text;
        private FriendViewHolder(View v) {
            super(v);

            friendImageView = (ImageView) itemView.findViewById(R.id.iconlivee);
            text=(TextView)itemView.findViewById(R.id.texttextname);

        }
    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

}
