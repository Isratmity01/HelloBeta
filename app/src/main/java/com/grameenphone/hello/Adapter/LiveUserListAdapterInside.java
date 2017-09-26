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
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class LiveUserListAdapterInside extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> users;
    private LayoutInflater inflater;
    private RequestManager glideRequest;
    private Fragment_Live fragment_live;
    private DatabaseHelper dbhelper;
    private User me;
    private  FriendViewHolder2 itemHolder;
    private final static int FADE_DURATION = 1000 ;// in milliseconds
    public LiveUserListAdapterInside(Context context, ArrayList<String> users, DatabaseHelper databaseHelper,Fragment_Live fragmentLive){
        this.context = context;
        this.users = users;
        this.dbhelper=databaseHelper;
        this.fragment_live=fragmentLive;
        this.me = dbhelper.getMe();

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_friend_for_live_screen, parent, false);
        final FriendViewHolder2 holder = new FriendViewHolder2(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        itemHolder = (FriendViewHolder2) holder;

        final User current = dbhelper.getUser( users.get(position) );
       // final ChatRoom gainedChatroom=dbhelper.getRoombyName(current.getName());
        if(current != null) {
            if(current.getPhotoUrl() != null && itemHolder.friendImageView2!=null)
            {

                Picasso.with((itemHolder.friendImageView2.getContext())).load( current.getPhotoUrl() ).transform(new jp.wasabeef.picasso.transformations.CropCircleTransformation())
                       .placeholder(R.drawable.hello1) .into(itemHolder.friendImageView2);
            }
            String lilname=current.getName().trim().split("\\s+")[0];
            itemHolder.text.setText(lilname);

        }

        setFadeAnimation(itemHolder.friendImageView2);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(current!=null && !(current.getUid()).equals(me.getUid()) ) {
                    final String chatRoomId = Compare.getRoomName(current.getUid(), me.getUid());

                    final ChatRoom chatRoom = dbhelper.getRoom(chatRoomId);

                    if ( chatRoom != null && chatRoom.getName() !=null ){
                        (fragment_live).openDialogue(current, chatRoom.getRequestStatus());
                    } else {
                        (fragment_live).openDialogue(current, 100);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }




    private class FriendViewHolder2 extends RecyclerView.ViewHolder {

    private ImageView friendImageView2;
        private TextView text;
        private FriendViewHolder2(View v) {
            super(v);

            friendImageView2 = (ImageView) itemView.findViewById(R.id.iconlivee);
            text=(TextView)itemView.findViewById(R.id.texttextname);

        }
    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

}
