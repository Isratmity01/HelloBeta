package com.grameenphone.hello.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grameenphone.hello.Fragments.Fragment_MainPage;
import com.grameenphone.hello.R;
import com.grameenphone.hello.model.ChatRoom;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class IncomingChatRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<ChatRoom> rooms = new ArrayList<ChatRoom>();

    private LayoutInflater inflater;
    private Fragment_MainPage fragment_mainPage;

    public IncomingChatRequestsAdapter(Context context, ArrayList<ChatRoom> rooms, Fragment_MainPage fragment_mainPage) {
        this.context = context;
        this.rooms = rooms;
        this.fragment_mainPage=fragment_mainPage;

    }

    public void clear() {
        int size = rooms.size();
        rooms.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_incoming_chat_request, parent, false);
        final ChatRoomHolder holder = new ChatRoomHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ChatRoomHolder itemHolder = (ChatRoomHolder) holder;
        final ChatRoom current = rooms.get(position);


        itemHolder.nameTextView.setText(current.getName());




        if (current.getPhotoUrl() != null) {

            Glide.with(itemHolder.roomImage.getContext()).load( current.getPhotoUrl() ).bitmapTransform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.hello1)
                    .into(itemHolder.roomImage);

        } else {
            itemHolder.roomImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.hello1));
        }


        itemHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,"আপনি ম্যাসেজ রিকুয়েস্ট এক্সেপ্ট করেছেন", Toast.LENGTH_SHORT).show();

            }
        });

        itemHolder.rejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,"আপনি ম্যাসেজ রিকুয়েস্ট রিজেক্ট করেছেন", Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public int getItemCount() {
        return rooms.size();
    }


    //return the filter class object


    private class ChatRoomHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView roomImage;
        private ImageView acceptRequest;
        private ImageView rejectRequest;

        private ChatRoomHolder(View v) {
            super(v);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            roomImage = (ImageView) itemView.findViewById(R.id.friendImageView);


            acceptRequest = (ImageView) itemView.findViewById(R.id.request_accept);
            rejectRequest = (ImageView) itemView.findViewById(R.id.request_reject);

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
