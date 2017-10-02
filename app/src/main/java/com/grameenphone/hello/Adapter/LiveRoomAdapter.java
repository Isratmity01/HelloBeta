package com.grameenphone.hello.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import com.grameenphone.hello.Adapter.LiveRoomViewHolder.LiveBotHolder;
import com.grameenphone.hello.Adapter.LiveRoomViewHolder.LiveImageHolder;
import com.grameenphone.hello.Adapter.LiveRoomViewHolder.LiveMsgHolder;
import com.grameenphone.hello.Adapter.LiveRoomViewHolder.LiveStickerHolder;
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
import java.io.FileOutputStream;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.grameenphone.hello.Utils.Userlevels.getLevelName;

public class LiveRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String CHAT_ROOMS_CHILD = "chat_rooms";
    private final int LIVE_IMG = 1;
    private final int LIVE_TXT = 2;
    private final int LIVE_STICKER = 3;
    private final int LIVE_BOT = 4;
    File wallpaperDirectory;
    private ArrayList<Chat> chats;
    private User sender;
    private User receiver;
    private Context context;
    private String FilePath = "https://firebasestorage.googleapis.com/v0/b/mars-e7047.appspot.com/o/";
    private Boolean downClicked = false;
    private String room;
    //UrlDetector parser = new UrlDetector();
    //List<Url> found;
    private DatabaseHelper dbHelper;
    private DatabaseReference mFirebaseDatabaseReference;
    private String drawable;
    private Uri imageUri;
    private String filename;
    private File finalfile;
    private String path;
    private ArrayList<Chat> chatArrayList = new ArrayList<Chat>();
    private Bitmap bitmapfinal;
    private LayoutInflater inflater;
    private Fragment_Live fragment_live;
    private DatabaseHelper databaseHelper;

    public LiveRoomAdapter(Context context, ArrayList<Chat> rooms, Fragment_Live fragmentLive, DatabaseHelper databaseHelper1) {
        this.context = context;
        this.chats = rooms;
        this.fragment_live = fragmentLive;
        this.databaseHelper = databaseHelper1;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder livebot = null;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case LIVE_IMG:
                View v1 = layoutInflater.inflate(R.layout.item_live_image, viewGroup, false);
                livebot = new LiveImageHolder(v1);
                break;
            case LIVE_STICKER:
                View v3 = layoutInflater.inflate(R.layout.item_live_sticker, viewGroup, false);
                livebot = new LiveStickerHolder(v3);
                break;
            case LIVE_TXT:
                View v8 = layoutInflater.inflate(R.layout.item_live_messsage, viewGroup, false);
                livebot = new LiveMsgHolder(v8);
                break;
            case LIVE_BOT:
                View v9 = layoutInflater.inflate(R.layout.item_live_bot, viewGroup, false);
                livebot = new LiveBotHolder(v9);
                break;


        }
        return livebot;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case LIVE_IMG:
                LiveImageHolder liveimgholder = (LiveImageHolder) holder;
                configureLiveImageHolder(liveimgholder, position);
                break;
            case LIVE_TXT:
                LiveMsgHolder livemsgholder = (LiveMsgHolder) holder;
                configureLiveMessageHolder(livemsgholder, position);
                break;
            case LIVE_STICKER:
                LiveStickerHolder livestickerholder = (LiveStickerHolder) holder;
                configureLiveStickerHolder(livestickerholder, position);
                break;
            case LIVE_BOT:
                LiveBotHolder livebot = (LiveBotHolder) holder;
                configureLiveBotHolder(livebot, position);
                break;


        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.get(position).getMessageType().equals("txt") && !chats.get(position).getSenderUid().equals("system_bot")) {
            return LIVE_TXT;
        } else if (chats.get(position).getMessageType().equals("stk") && !chats.get(position).getSenderUid().equals("system_bot")) {

            return LIVE_STICKER;
        } else if (chats.get(position).getMessageType().equals("Image") && !chats.get(position).getSenderUid().equals("system_bot")) {

            return LIVE_IMG;
        } else
            return LIVE_BOT;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void configureLiveStickerHolder(LiveStickerHolder livestickerholder, final int position) {
        if (chats.get(position) != null) {
            User user = databaseHelper.getUser(chats.get(position).getSenderUid());

            String level = getLevelName(user.getUserpoint());
            if (level.equals("লেভেল ১")) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.level_1);
            } else if (level.equals("লেভেল ২")) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.level_2);
            } else if (level.equals("লেভেল ৩")) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.level_3);
            } else if (level.equals("লেভেল ৪")) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.level_4);
            } else if (level.equals("লেভেল ৫")) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.level_5);
            }


            if (user.isMod() == 1) {
                livestickerholder.Badge.setBackgroundResource(R.drawable.moderator);
            }


            path = chats.get(position).getMessage();
            path = path.substring(path.lastIndexOf(".") + 1);
            livestickerholder.getpName()
                    .setText(
                            chats.get(position).getSender().trim().split("\\s+")[0]
                    );
            Glide.with(context).load(getImage(path)).into(livestickerholder.liveSticker);

            livestickerholder.getTimestamp()
                    .setText(DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );

            livestickerholder.pName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = databaseHelper.getUser(chats.get(position).getSenderUid());
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


        }
    }

    private void configureLiveMessageHolder(LiveMsgHolder livemsgholder, final int position) {
        if (chats.get(position) != null) {

            String message = chats.get(position).getMessage();

            livemsgholder.liveText.setTextColor(context.getResources().getColor(R.color.text_black));
            livemsgholder.liveText.setTypeface(null, Typeface.NORMAL);

            User user = databaseHelper.getUser(chats.get(position).getSenderUid());

            String level = getLevelName(user.getUserpoint());
            if (level.equals("লেভেল ১")) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.level_1);
            } else if (level.equals("লেভেল ২")) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.level_2);
            } else if (level.equals("লেভেল ৩")) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.level_3);
            } else if (level.equals("লেভেল ৪")) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.level_4);
            } else if (level.equals("লেভেল ৫")) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.level_5);
            }

            if (user.isMod() == 1) {
                livemsgholder.Badge.setBackgroundResource(R.drawable.moderator);
            }


            //  parser = new UrlDetector(chats.get(position).getMessage(), UrlDetectorOptions.Default);
            //found = parser.detect();
            livemsgholder.pName.setText(chats.get(position).getSender().trim().split("\\s+")[0]);

            if (message.equals("মডারেটর মেসেজটি ডিলিট করেছেন")) {
                livemsgholder.liveText.setTextColor(context.getResources().getColor(R.color.text_gray));
                livemsgholder.liveText.setTypeface(livemsgholder.liveText.getTypeface(), Typeface.ITALIC);
            }

            livemsgholder.getLiveText().setText(message);

            livemsgholder.getTimestamp()
                    .setText(DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            ));

            livemsgholder.pName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = databaseHelper.getUser(chats.get(position).getSenderUid());
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
        }


        livemsgholder.liveText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                User meuser = databaseHelper.getMe();
                User user = databaseHelper.getUser(chats.get(position).getSenderUid());


                if (meuser.isMod() == 1) {

                    if (!meuser.getUid().equals(user.getUid())) {

                        (fragment_live).openModDialogue(user, chats.get(position));

                    } else {

                        (fragment_live).openModDialogue(meuser, chats.get(position));

                    }
                }


                return false;
            }
        });
    }

    private void configureLiveImageHolder(final LiveImageHolder liveimgholder, final int position) {
        downClicked = false;
        if (chats.get(position) != null) {
            User user = databaseHelper.getUser(chats.get(position).getSenderUid());

            String level = getLevelName(user.getUserpoint());
            if (level.equals("লেভেল ১")) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.level_1);
            } else if (level.equals("লেভেল ২")) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.level_2);
            } else if (level.equals("লেভেল ৩")) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.level_3);
            } else if (level.equals("লেভেল ৪")) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.level_4);
            } else if (level.equals("লেভেল ৫")) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.level_5);
            }

            if (user.isMod() == 1) {
                liveimgholder.Badge.setBackgroundResource(R.drawable.moderator);
            }

            liveimgholder.pName.setText(chats.get(position).getSender().trim().split("\\s+")[0]);
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/Hello/HelloImages");
            filename = chats.get(position).getUrl_file();
            String photoUrl = filename.substring(filename.lastIndexOf("/") + 1);

            finalfile = new File(wallpaperDirectory.getAbsolutePath() + "/" + photoUrl + ".png");
            imageUri = Uri.fromFile(finalfile);
            if (finalfile.exists()) {

                Glide.with(context).load(imageUri).bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0)).into(liveimgholder.liveImage);
                liveimgholder.downloadImage.setVisibility(View.GONE);
            } else {
                liveimgholder.downloadImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.pictures).bitmapTransform(new BlurTransformation(context), new RoundedCornersTransformation(context, 45, 0)).into(liveimgholder.liveImage);

            }


            liveimgholder.getTimestamp()
                    .setText(DateTimeUtility
                            .getFormattedTimeFromTimestamp(chats.get(position).getTimestamp())
                    );
            liveimgholder.liveImage.setOnClickListener(new View.OnClickListener() {
                //Start new list activity

                public void onClick(View v) {

                    path = chats.get(position).getUrl_file();
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                            .into(new SimpleTarget<Bitmap>(300, 300) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                    bitmapfinal = resource;

                                    liveimgholder.liveImage.setImageBitmap(resource);
                                }
                            });

                    ImageDialog imageDialog = new ImageDialog(context);
                    // imageDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    imageDialog.start(context, path, "no");
                    liveimgholder.downloadImage.setVisibility(View.GONE);
                }
            });


        }
        liveimgholder.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downClicked = true;
                if (chats.get(position).getUrl_file() == null) {
                    liveimgholder.liveImage.setImageDrawable(ContextCompat.getDrawable(context,
                            R.drawable.ic_happy));
                } else {
                    path = chats.get(position).getUrl_file();
                    Glide.with(context)
                            .load(path)
                            .asBitmap()
                            .transform(new CenterCrop(context), new RoundedCornersTransformation(context, 45, 0))
                            .into(new SimpleTarget<Bitmap>(300, 300) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    // liveimgholder.progressBar.setVisibility(View.GONE);
                                    bitmapfinal = resource;

                                    liveimgholder.liveImage.setImageBitmap(resource);
                                }
                            });


                       /* Glide.with(context)
                                .load(chats.get(position).getFile().getUrl_file()).bitmapTransform(new RoundedCornersTransformation(context, 30, 0,
                                RoundedCornersTransformation.CornerType.BOTTOM))

                                .into(receiverImageHolder.imageFileView);*/
                    liveimgholder.downloadImage.setVisibility(View.GONE);

                }
            }
        });
        liveimgholder.pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = databaseHelper.getUser(chats.get(position).getSenderUid());
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
    }


    public int getImage(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }

    private void configureLiveBotHolder(final LiveBotHolder livebot, final int position) {

        if (chats.get(position) != null) {
            livebot.Badge.setBackgroundResource(R.drawable.bot);


            livebot.liveText.setTypeface(null, Typeface.NORMAL);
            //  parser = new UrlDetector(chats.get(position).getMessage(), UrlDetectorOptions.Default);
            //found = parser.detect();
            String name = chats.get(position).getSender();

            String message = chats.get(position).getMessage();

            if (name.equals("weather_bot")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.weatherbotcolor));
            } else if (name.equals("quiz_bot")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.quizbotcolor));
            } else if (name.equals("cric_bot")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.cricbotcolor));
            } else if (name.equals("deal_bot")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.dealbotcolor));
            } else if (name.equals("hello_bot")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.hellobotcolor));
            }

            if (message.equals("মডারেটর মেসেজটি ডিলিট করেছেন")) {
                livebot.liveText.setTextColor(context.getResources().getColor(R.color.text_gray));
                livebot.liveText.setTypeface(livebot.liveText.getTypeface(), Typeface.ITALIC);
            }
            livebot.pName.setText(name);


            livebot.liveText.setText(message);

            livebot.getTimestamp()
                    .setText(DateTimeUtility
                            .getFormattedTimeFromTimestamp(
                                    chats.get(position).getTimestamp()
                            )
                    );



            livebot.liveText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    User meuser = databaseHelper.getMe();
                    User user = databaseHelper.getUser(chats.get(position).getSenderUid());


                    if (meuser.isMod() == 1) {

                        if (!meuser.getUid().equals(user.getUid())) {

                            (fragment_live).openModDialogue(user, chats.get(position));

                        } else {

                            (fragment_live).openModDialogue(meuser, chats.get(position));

                        }
                    }


                    return false;
                }
            });


        }

    }


    public void createImageFromBitmap(Bitmap bmp, String Url) {

        FileOutputStream fileOutputStream = null;
        try {

            // create a File object for the parent directory
            wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/Hello/HelloImages");
            // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();

            Url = Url.substring(Url.lastIndexOf("/") + 1);
            String fname = Url + ".png";
            File file = new File(wallpaperDirectory, fname);
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(context, "ইমেজ সেভ হয়েছে", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
            } else {
                Toast.makeText(context, "অলরেডি সেভ করা আছে", Toast.LENGTH_SHORT).show();
            }

        } finally {

        }
    }
}
