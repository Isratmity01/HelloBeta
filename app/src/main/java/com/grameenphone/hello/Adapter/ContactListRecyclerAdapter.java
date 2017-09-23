package com.grameenphone.hello.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.grameenphone.hello.Fragments.Fragment_Contacts;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.SelectUser;
import com.grameenphone.hello.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by shadman.rahman on 11-Jun-17.
 */

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<SelectUser> _data;
    private ArrayList<SelectUser> arraylist;
    Context _c;
    private LayoutInflater inflater;

    private ImageView roundedImage;
    private Fragment_Contacts fragment_contacts;

    private DatabaseHelper dbHelper;
    private User me;

    private final static int FADE_DURATION = 1000 ;// in milliseconds
    public ContactListRecyclerAdapter(List<SelectUser> selectUsers, Context context,Fragment_Contacts fragmentContacts) {
        _data = selectUsers;
        _c = context;
        this.fragment_contacts=fragmentContacts;
        this.arraylist = new ArrayList<SelectUser>();
        this.arraylist.addAll(_data);

        dbHelper = new DatabaseHelper(_c);
        me = dbHelper.getMe();

    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(_c).inflate(R.layout.contact_list_item, parent, false);
        final FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FriendViewHolder itemHolder = (FriendViewHolder) holder;

        final SelectUser data = (SelectUser) _data.get(position);
        itemHolder.title.setText(data.getName());

        itemHolder.phone.setText(data.getPhone());

        // Set image if exists
        try {

            if (data.getThumb() != null) {
                Glide.with(itemHolder.imageView.getContext())
                        .load(data.getThumb()).bitmapTransform(new CropCircleTransformation(_c))
                        .into(itemHolder.imageView);
            } else {
                itemHolder.imageView.setImageResource(
                        R.drawable.hello1);
            }
            // Seting round image


        } catch (OutOfMemoryError e) {
            // Add default picture
            itemHolder.imageView.setImageResource(R.drawable.hello1);
            e.printStackTrace();
        }

            itemHolder.invitation.setText("হ্যালো বলুন");


     //   Log.e("Image Thumb", "--------------" + data.getThumb());



        itemHolder.itemView.setTag(data);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                User current = new User(data.getUid(),data.getName(),data.getPhone(),data.getThumb());

                if(current!=null && !(current.getUid()).equals(me.getUid()) ) {
                    final String chatRoomId = Compare.getRoomName(current.getUid(), me.getUid());

                    final ChatRoom chatRoom = dbHelper.getRoom(chatRoomId);

                    if ( chatRoom != null && chatRoom.getName() !=null ){
                        (fragment_contacts).openDialogue(current, chatRoom.getRequestStatus());
                    } else {
                        (fragment_contacts).openDialogue(current, 100);
                    }
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return _data.size();
    }




    private class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title,phone;
        private TextView invitation;
        private FriendViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.nameContactView);
            //     v.check = (CheckBox) view.findViewById(R.id.check);
            phone = (TextView) v.findViewById(R.id.contactnumber);
            invitation=(TextView)v.findViewById(R.id.time_stamp_un_read_message);
            imageView = (ImageView) v.findViewById(R.id.contacthead);

        }
    }


}