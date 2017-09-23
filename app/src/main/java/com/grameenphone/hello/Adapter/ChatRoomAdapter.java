package com.grameenphone.hello.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grameenphone.hello.Adapter.chatroomviewholder.ReceiverImageHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.ReceiverMessageHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.ReceiverStickerHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.SenderImageHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.SenderMessageHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.SenderStickerHolder;
import com.grameenphone.hello.Adapter.chatroomviewholder.SystemMessageHolder;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.DateTimeUtility;
import com.grameenphone.hello.Utils.ImageDialog;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Chat> chats;
    private User sender;
    private User receiver;
    private Context context;
    private String FilePath="https://firebasestorage.googleapis.com/v0/b/mars-e7047.appspot.com/o/";
    private Boolean downClicked=false;
    private String room;
    //UrlDetector parser = new UrlDetector();
    //List<Url> found;
    private DatabaseHelper dbHelper;
    private Bitmap bitmapfinal;

    private DatabaseReference mFirebaseDatabaseReference;
    public static String CHAT_ROOMS_CHILD = "chat_rooms";

     private String drawable;
    private Uri imageUri;
    private String filename;
    private  File finalfile;
    private  String path;

    private final int SENDER_IMG = 1;
    private final int RECEIVER_IMG = 2;
    private final int SENDER_MSG = 3;
    private final int RECEIVER_MSG = 4;
    private final int SYSTEM_MSG = 5;
    private final int SENDER_DOC = 6;
    private final int RECEIVER_DOC = 7;
    private final int SENDER_STICKER = 8;
    private final int RECEIVER_STICKER = 9;
    File  wallpaperDirectory;
    public ChatRoomAdapter(Context context, ArrayList<Chat> chats, User sender, User receiver, String room){
        this.context = context;
        this.chats = chats;
        this.receiver = receiver;
        this.sender = sender;

        this.room = room;

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        dbHelper = new DatabaseHelper(context);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType){
            case RECEIVER_MSG:
                View v1 = layoutInflater.inflate(R.layout.item_receiver_message, viewGroup, false);
                viewHolder = new ReceiverMessageHolder(v1);
                break;
            case SENDER_MSG:
                View v3 = layoutInflater.inflate(R.layout.item_sender_message, viewGroup, false);
                viewHolder = new SenderMessageHolder(v3);
                break;
            case RECEIVER_STICKER:
                View v8 = layoutInflater.inflate(R.layout.item_receiver_sticker, viewGroup, false);
                viewHolder = new ReceiverStickerHolder(v8);
                break;
            case SENDER_STICKER:
                View v9 = layoutInflater.inflate(R.layout.item_sender_sticker, viewGroup, false);
                viewHolder = new SenderStickerHolder(v9);
                break;
            case RECEIVER_IMG:
                View v10 = layoutInflater.inflate(R.layout.item_receiver_img, viewGroup, false);
                viewHolder = new ReceiverImageHolder(v10);
                break;
            case SENDER_IMG:
                View v99 = layoutInflater.inflate(R.layout.item_sender_img, viewGroup, false);
                viewHolder = new SenderImageHolder(v99);
                break;
            default:
                View v5 = layoutInflater.inflate(R.layout.item_system_message, viewGroup, false);
                viewHolder = new SystemMessageHolder(v5);
                break;
        }
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case RECEIVER_MSG:
                ReceiverMessageHolder receiverMessageHolder = (ReceiverMessageHolder) holder;
                configureReceiverMessageHolder(receiverMessageHolder, position);
                break;
            case SENDER_MSG:
                SenderMessageHolder senderMessageHolder = (SenderMessageHolder) holder;
                configureSenderMessageHolder(senderMessageHolder, position);
                break;
            case RECEIVER_STICKER:
                ReceiverStickerHolder receiverStickerHolder = (ReceiverStickerHolder) holder;
                configureReceiverStickerHolder(receiverStickerHolder, position);
                break;
            case SENDER_STICKER:
                SenderStickerHolder senderStickerHolder = (SenderStickerHolder) holder;
                configureSenderStickerHolder(senderStickerHolder, position);
                break;
            case RECEIVER_IMG:
                ReceiverImageHolder receiverImageHolder = (ReceiverImageHolder) holder;
                configureReceiverImageHolder(receiverImageHolder, position);
                break;
            case SENDER_IMG:
                SenderImageHolder senderImageHolder = (SenderImageHolder) holder;
                configureSenderImageHolder(senderImageHolder, position);
                break;
            default:
                SystemMessageHolder systemMessageHolder = (SystemMessageHolder) holder;
                configureSystemMessageHolder(systemMessageHolder, position);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position){
        if(chats.get(position).getMessageType().equals("txt")){
            if (chats.get(position).getMessage() != null && !chats.get(position).getSenderUid().equals(sender.getUid())){
                return RECEIVER_MSG;
            }
            else return SENDER_MSG;
        }
        else if(chats.get(position).getMessageType().equals("stk")){
            if (chats.get(position).getMessage() != null && !chats.get(position).getSenderUid().equals(sender.getUid())){
                return RECEIVER_STICKER;
            }
            else return SENDER_STICKER;
        }
        else if(chats.get(position).getMessageType().equals("img"))
        {
            if( !chats.get(position).getSenderUid().equals(sender.getUid()) ){
                return RECEIVER_IMG;
            }
            else return SENDER_IMG;
        }

        else return SYSTEM_MSG;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverMessageHolder(ReceiverMessageHolder receiverMessageHolder, int position){

        if(receiver != null){
            receiverMessageHolder.getReceiversMessage()
                    .setText(
                            chats.get(position).getMessage()
                    );

            receiverMessageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );

            if(chats.get(position).getReadStatus() == 0){


                Chat data = chats.get(position);


                data.setReadStatus(1);
             //   dbHelper.addMessage(data, data.getChatId());
                data.setReadStatus(1);
                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(room)
                        .child(data.getChatId())
                        .updateChildren(readstatus);


            }



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverStickerHolder(ReceiverStickerHolder receiverStickerHolder, int position){
        if(receiver != null){


            path = chats.get(position).getMessage();
            path=path.substring(path.lastIndexOf(".") + 1);

            Glide.with(context).load(getImage(path)).into(receiverStickerHolder.sticker);

            receiverStickerHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );

            if(chats.get(position).getReadStatus() == 0){


                Chat data = chats.get(position);

                data.setReadStatus(1);
                //dbHelper.addMessage(data, data.getChatId());


                data.setReadStatus(1);
                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(room)
                        .child(data.getChatId())
                        .updateChildren(readstatus);


            }



        }
    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureReceiverImageHolder(final ReceiverImageHolder receiverImageHolder, final int position){
        downClicked=false;
        if(receiver != null){

            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/Hello/HelloImages");
            filename=chats.get(position).getFile().getUrl_file();
            String  photoUrl=filename.substring(filename.lastIndexOf("/") + 1);

            finalfile = new File(wallpaperDirectory.getAbsolutePath() + "/"+photoUrl+".png");
            imageUri = Uri.fromFile(finalfile);
            if(finalfile.exists()){

                Glide.with(context).load(imageUri).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0)).into(receiverImageHolder.imageFileView);
                receiverImageHolder.downloadImage.setVisibility(View.GONE);
            }
            else {
                receiverImageHolder.downloadImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.pictures).bitmapTransform(new BlurTransformation(context), new RoundedCornersTransformation(context, 45, 0)).into(receiverImageHolder.imageFileView);

            }


            receiverImageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(chats.get(position).getTimestamp())
                    );
            receiverImageHolder.imageFileView.setOnClickListener(new View.OnClickListener() {
                //Start new list activity

                public void onClick(View v) {
                    receiverImageHolder.progressBar.setVisibility(View.VISIBLE);
                    path=chats.get(position).getFile().getUrl_file();
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                            .into(new SimpleTarget<Bitmap>(300,300) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    receiverImageHolder.progressBar.setVisibility(View.GONE);
                                    bitmapfinal=resource;

                                    receiverImageHolder.imageFileView.setImageBitmap(resource);
                                }
                            });

                    ImageDialog imageDialog=new ImageDialog(context);
                   // imageDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    imageDialog.start(context, path,"no");
                }
            });
            if(chats.get(position).getReadStatus() == 0){


                Chat data = chats.get(position);

                data.setReadStatus(1);
              //  dbHelper.addMessage(data, data.getChatId());

                data.setReadStatus(1);
                Map<String, Object> readstatus = new HashMap<>();
                readstatus.put("readStatus",1);

                mFirebaseDatabaseReference.child(CHAT_ROOMS_CHILD)
                        .child(room)
                        .child(data.getChatId())
                        .updateChildren(readstatus);


            }



        }
        receiverImageHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downClicked=true;
                receiverImageHolder.progressBar.setVisibility(View.VISIBLE);
                if (chats.get(position).getFile().getUrl_file() == null) {
                    receiverImageHolder.imageFileView.setImageDrawable(ContextCompat.getDrawable(context,
                            R.drawable.ic_happy));
                    receiverImageHolder.progressBar.setVisibility(View.GONE);
                } else {
                    path=chats.get(position).getFile().getUrl_file();
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                            .into(new SimpleTarget<Bitmap>(300,300) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    receiverImageHolder.progressBar.setVisibility(View.GONE);
                                    bitmapfinal=resource;

                                    receiverImageHolder.imageFileView.setImageBitmap(resource);
                                }
                            });


                       /* Glide.with(context)
                                .load(chats.get(position).getFile().getUrl_file()).bitmapTransform(new RoundedCornersTransformation(context, 30, 0,
                                RoundedCornersTransformation.CornerType.BOTTOM))

                                .into(receiverImageHolder.imageFileView);*/
                    receiverImageHolder.downloadImage.setVisibility(View.GONE);

                }
            }
        });
    }








    private void configureSenderMessageHolder(SenderMessageHolder senderMessageHolder, int position){
        if(sender != null){
          //  parser = new UrlDetector(chats.get(position).getMessage(), UrlDetectorOptions.Default);
            //found = parser.detect();

            senderMessageHolder.getSendersMessage()
                    .setText(
                            chats.get(position).getMessage()
                    );
            senderMessageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );

            if (chats.get(position).getReadStatus() == 0) {
                senderMessageHolder.deliveryStatus.setImageResource(
                        R.drawable.ic_delivered);
            } else {
                senderMessageHolder.deliveryStatus.setImageResource(
                        R.drawable.seen_status);

            }
        }
    }
    private void configureSenderStickerHolder(SenderStickerHolder senderStickerHolder, int position){
        if(sender != null){
            path =chats.get(position).getMessage();
            path=path.substring(path.lastIndexOf(".") + 1);
            Glide.with(context).load(getImage(path)).into(senderStickerHolder.sticker);
            senderStickerHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            if (chats.get(position).getReadStatus() == 0) {
                senderStickerHolder.deliveryStatus.setImageResource(
                       R.drawable.ic_delivered);
            } else {
                senderStickerHolder.deliveryStatus.setImageResource(
                        R.drawable.seen_status);

            }
        }
    }



    public int getImage(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }

    private void configureSenderImageHolder(final SenderImageHolder senderImageHolder, final int position){

        if(sender != null){
            //    int path= Integer.parseInt(chats.get(position).getFile().getUrl_file());
            if (chats.get(position).getFile().getUrl_file() == null) {
                senderImageHolder.imageFileView.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_happy));
            } else {

                Glide.with(context)
                        .load(chats.get(position).getFile().getUrl_file()).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                        .into(senderImageHolder.imageFileView);

            }

            //  Glide.with(context).load(chats.get(position).getFile().getUrl_file()).into(senderImageHolder.Img);
            senderImageHolder.getTimestamp()
                    .setText( DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );
            if (chats.get(position).getReadStatus() == 0) {
                senderImageHolder.deliveryStatus.setImageResource(
                       R.drawable.ic_delivered);
            } else {
                senderImageHolder.deliveryStatus.setImageResource(
                        R.drawable.seen_status);

            }
        }
        senderImageHolder.imageFileView.setOnClickListener(new View.OnClickListener() {
            //Start new list activity

            public void onClick(View v) {
                path=chats.get(position).getFile().getUrl_file();
                ImageDialog imageDialog=new ImageDialog(context);
                // imageDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                imageDialog.start(context, path,"no");

            }
        });

    }










    private void configureSystemMessageHolder(SystemMessageHolder systemMessageHolder, int position){
        if(sender != null){
            systemMessageHolder.getSystemMsg().setText(chats.get(position).getMessage());
        }
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
                    Toast.makeText(context,"ইমেজ সেভ হয়েছে",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
            }
            else {
                Toast.makeText(context, "অলরেডি সেভ করা আছে", Toast.LENGTH_SHORT).show();
            }

        } finally {

        }
    }
}
