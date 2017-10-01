package com.grameenphone.hello.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import com.grameenphone.hello.Activities.MainActivity;
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
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.grameenphone.hello.Utils.Userlevels.getLevelName;


public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String path;
    File wallpaperDirectory;
    private Uri imageUri;
    private String filename;
    private File finalfile;
    private String message;
    private String FilePath = "https://firebasestorage.googleapis.com/v0/b/mars-e7047.appspot.com/o/";
    private Context context;
    private ArrayList<Chat> chatArrayList = new ArrayList<Chat>();
    private Bitmap bitmapfinal;
    private LayoutInflater inflater;
    private Fragment_Live fragment_live;
    private DatabaseHelper databaseHelper;

    private User meuser;


    public LiveListAdapter(Context context, ArrayList<Chat> rooms, Fragment_Live fragmentLive, DatabaseHelper databaseHelper) {
        this.context = context;
        this.chatArrayList = rooms;
        this.fragment_live = fragmentLive;
        this.databaseHelper = databaseHelper;

        meuser = databaseHelper.getMe();

    }

    public void clear() {
        int size = chatArrayList.size();
        chatArrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.from(context).inflate(R.layout.item_live_messsage, parent, false);
        final MessageViewHolder holder = new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final MessageViewHolder viewHolder = (MessageViewHolder) holder;
        final Chat chat = chatArrayList.get(position);
        String lilname = chat.getSender().trim().split("\\s+")[0];
        viewHolder.badge.setVisibility(View.VISIBLE);

        if(meuser.isMod() == 1) {
            viewHolder.badge.setBackgroundResource(R.drawable.moderator);
        }

        if (chat.getSenderUid().equals("system_bot")) {
            viewHolder.badge.setBackgroundResource(R.drawable.bot);
        } else {
            User user = databaseHelper.getUser(chat.getSenderUid());

            String level = getLevelName(user.getUserpoint());
            if (level.equals("লেভেল ১")) {
                viewHolder.badge.setBackgroundResource(R.drawable.level_1);
            } else if (level.equals("লেভেল ২")) {
                viewHolder.badge.setBackgroundResource(R.drawable.level_2);
            } else if (level.equals("লেভেল ৩")) {
                viewHolder.badge.setBackgroundResource(R.drawable.level_3);
            } else if (level.equals("লেভেল ৪")) {
                viewHolder.badge.setBackgroundResource(R.drawable.level_4);
            } else if (level.equals("লেভেল ৫")) {
                viewHolder.badge.setBackgroundResource(R.drawable.level_5);
            } else {
                viewHolder.badge.setBackgroundResource(R.drawable.level_1);
            }
        }






        if (chat.getMessageType().equals("txt") && !chat.getSenderUid().equals("system_bot")) {

            viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.text_black));

            if (chat.getMessage() != null) {
                viewHolder.sticker.setVisibility(View.GONE);
                viewHolder.imagedownload.setVisibility(View.GONE);
                viewHolder.image.setVisibility(View.GONE);
                String message = chat.getMessage();
                viewHolder.liveuser.setVisibility(View.VISIBLE);

                String name = lilname;
                viewHolder.liveuser.setText(name);

                if(message.equals("মডারেটর মেসেজটি ডিলিট করেছেন")){
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.text_gray));
                    viewHolder.liveMessage.setTypeface(viewHolder.liveMessage.getTypeface(), Typeface.ITALIC);
                }


                viewHolder.liveMessage.setText(message);
                viewHolder.liveMessage.setVisibility(View.VISIBLE);

                viewHolder.liveTime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));


            }


        }
        if (chat.getMessageType().equals("stk") && !chat.getSenderUid().equals("system_bot")) {

            if (chat.getMessage() != null) {
                viewHolder.sticker.setVisibility(View.VISIBLE);
                viewHolder.imagedownload.setVisibility(View.GONE);
                viewHolder.image.setVisibility(View.GONE);
                viewHolder.liveuser.setVisibility(View.VISIBLE);
                String name = lilname;
                String message = chat.getMessage();

                viewHolder.liveuser.setText(name);
                viewHolder.liveMessage.setVisibility(View.GONE);
                String path = chat.getMessage();
                path = path.substring(path.lastIndexOf(".") + 1);
                Glide.with(context).load(getImage(path)).into(viewHolder.sticker);

                viewHolder.liveTime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));


            }


        }
        if (chat.getSender().contains("_bot") && chat.getSenderUid().equals("system_bot")) {

            if (chat.getMessage() != null) {
                viewHolder.sticker.setVisibility(View.GONE);
                viewHolder.imagedownload.setVisibility(View.GONE);
                viewHolder.image.setVisibility(View.GONE);
                String name = chat.getSender();

                message =  chat.getMessage() ;

                if (name.equals("weather_bot")) {
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.weatherbotcolor));
                } else if (name.equals("quiz_bot")) {
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.quizbotcolor));
                } else if (name.equals("cric_bot")) {
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.cricbotcolor));
                } else if (name.equals("deal_bot")) {
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.dealbotcolor));
                } else if (name.equals("hello_bot")) {
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.hellobotcolor));
                }

                if(message.equals("মডারেটর মেসেজটি ডিলিট করেছেন")){
                    viewHolder.liveMessage.setTextColor(context.getResources().getColor(R.color.text_gray));
                    viewHolder.liveMessage.setTypeface(viewHolder.liveMessage.getTypeface(), Typeface.ITALIC);
                }


                viewHolder.liveuser.setText(name);


                viewHolder.liveMessage.setText(message);

                viewHolder.liveMessage.setVisibility(View.VISIBLE);
                viewHolder.liveTime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));


            }


        }
        if (chat.getMessageType().equals("Image")) {


            viewHolder.sticker.setVisibility(View.GONE);
            viewHolder.imagedownload.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.VISIBLE);

            viewHolder.liveuser.setVisibility(View.VISIBLE);
            String name = lilname;
            viewHolder.liveuser.setText(name);
            viewHolder.liveMessage.setVisibility(View.GONE);
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/Hello/HelloImages");
            filename = chat.getUrl_file();
            String photoUrl = filename.substring(filename.lastIndexOf("/") + 1);

            finalfile = new File(wallpaperDirectory.getAbsolutePath() + "/" + photoUrl + ".png");
            imageUri = Uri.fromFile(finalfile);
            if (finalfile.exists()) {

                Glide.with(context).load(imageUri).placeholder(R.drawable.pictures).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0)).into(viewHolder.image);
                viewHolder.imagedownload.setVisibility(View.GONE);
            } else {
                viewHolder.imagedownload.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.pictures).bitmapTransform(new BlurTransformation(context), new RoundedCornersTransformation(context, 45, 0)).into(viewHolder.image);

            }
            path = chat.getUrl_file();

            Glide.with(context).load(path).into(viewHolder.sticker);

            viewHolder.liveTime.setText(DateTimeUtility.getFormattedTimeFromTimestamp(chat.getTimestamp()));

        }
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            //Start new list activity

            public void onClick(View v) {


                Glide.with(context)
                        .load(path)
                        .asBitmap()
                        .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                        .into(new SimpleTarget<Bitmap>(300, 300) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                bitmapfinal = resource;

                                viewHolder.image.setImageBitmap(resource);
                                viewHolder.imagedownload.setVisibility(View.GONE);
                            }
                        });

                ImageDialog imageDialog = new ImageDialog(context);
                // imageDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                imageDialog.start(context, path, "no");
            }
        });

        viewHolder.liveuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = databaseHelper.getUser(chat.getSenderUid());
                User meuser = databaseHelper.getMe();
                if (!meuser.getUid().equals(user.getUid())) {

                    if (user.getUid() != null) {
                        final String chatRoomId = Compare.getRoomName(user.getUid(), meuser.getUid());

                        final ChatRoom chatRoom = databaseHelper.getRoom(chatRoomId);

                        if (chatRoom != null && chatRoom.getName() != null) {
                            (fragment_live).openDialogue(user, chatRoom.getRequestStatus());
                        } else {
                            (fragment_live).openDialogue(user, 100);
                        }
                    }

                }


            }
        });




        viewHolder.liveMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                User user = databaseHelper.getUser(chat.getSenderUid());



                if(meuser.isMod() == 1) {

                    if (!meuser.getUid().equals(user.getUid())) {

                        (fragment_live).openModDialogue(user, chat);

                    } else {

                        (fragment_live).openModDialogue(meuser, chat);

                    }
                }



                return false;
            }
        });





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

    public void refresh() {
        //manipulate list
        notifyDataSetChanged();

    }

    public int refreshfromswipe() {
        //manipulate list
        notifyDataSetChanged();
        return 1;
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView liveMessage;
        public TextView liveTime;
        public TextView liveuser;
        public ImageView sticker;
        public ImageView image;
        public ImageView imagedownload;


        public ImageView badge;

        public MessageViewHolder(View v) {
            super(v);
            badge = (ImageView) itemView.findViewById(R.id.badge);
            liveMessage = (TextView) itemView.findViewById(R.id.live_message);
            liveTime = (TextView) itemView.findViewById(R.id.time_stamp_live);
            liveuser = (TextView) itemView.findViewById(R.id.liveusername);
            sticker = (ImageView) itemView.findViewById(R.id.livesticker);
            image = (ImageView) itemView.findViewById(R.id.liveimage);
            imagedownload = (ImageView) itemView.findViewById(R.id.livedownloader);
        }
    }

}
