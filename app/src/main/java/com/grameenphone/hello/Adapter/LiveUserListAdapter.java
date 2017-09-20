package com.grameenphone.hello.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.grameenphone.hello.Fragments.Fragment_MainPage;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.R;
import com.grameenphone.hello.model.User;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class LiveUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> usersid;
    private LayoutInflater inflater;
    private RequestManager glideRequest;

    private Fragment_MainPage fragmentMainPage;
    private DatabaseHelper dbHelper;

    private User me;

    private final static int FADE_DURATION = 1000 ;// in milliseconds
    public LiveUserListAdapter(Context context, ArrayList<String> usersid, DatabaseHelper db, Fragment_MainPage fragment_mainPage){
        this.context = context;
        this.usersid = usersid;
        this.dbHelper = db;
        this.fragmentMainPage=fragment_mainPage;

        this.me = dbHelper.getMe();

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_live, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;

        final User current = dbHelper.getUser( usersid.get(position) );
       // final ChatRoom gainedChatroom=dbHelper.getRoombyName(current.getName());
        if(current != null && current.getPhotoUrl() != null) {
            Glide.with(itemHolder.friendImageView.getContext()).load( current.getPhotoUrl() ).bitmapTransform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.hello1)
                    .into(itemHolder.friendImageView);
        }

        setFadeAnimation(itemHolder.friendImageView);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current!=null) {
                    final String chatRoomId = Compare.getRoomName(current.getUid(), me.getUid());

                    final ChatRoom chatRoom = dbHelper.getRoom(chatRoomId);

                    if ( chatRoom != null && chatRoom.getName() !=null ){
                        (fragmentMainPage).openDialogue(current, chatRoom.getRequestStatus());
                    } else {
                        (fragmentMainPage).openDialogue(current, 100);
                    }
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return usersid.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView friendImageView;

        private FriendViewHolder(View v) {
            super(v);

            friendImageView = (ImageView) itemView.findViewById(R.id.profile_image_live);

        }
    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

}
