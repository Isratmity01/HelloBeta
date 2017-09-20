package com.grameenphone.hello.Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.model.ChatRoom;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class LoadImage {

    private Context context;
    private String root;
    private File imageDir;

    private DatabaseHelper dbHelper;


    public LoadImage(Context context) {
        this.context = context;
        root = Environment.getExternalStorageDirectory().toString();
        imageDir = new File(root + "/Hello/Images/");

        dbHelper = new DatabaseHelper(context);
    }


    public Uri downloadProfileImage(final String imageUrl, String uid) {

        final Uri[] imgDrawable = {null};

        String[] uidextarr = ((imageUrl.split("attachments")[1]).split("_gallery")[0]).split("_");
        String uidext = uidextarr[uidextarr.length - 1];


        final String name = "roompic_" + uid + "_" + uidext + ".jpg";

        Picasso.with(context)
                .load(imageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {


                                  if (!imageDir.exists()) {
                                      imageDir.mkdirs();
                                  }

                                  File imageFile = new File(imageDir, name);

                                  FileOutputStream out = new FileOutputStream(imageFile);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                  imgDrawable[0] = Uri.fromFile(imageFile);

                                  out.flush();
                                  out.close();
                              } catch (Exception e) {
                                  // some action
                              }
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );


        return imgDrawable[0];


    }
    public Uri downloadImage(final String imageUrl) {

        final Uri[] imgDrawable = {null};

        String[] uidextarr = ((imageUrl.split("attachments")[1]).split("_gallery")[0]).split("_");
        String uidext = uidextarr[uidextarr.length - 1];


        final String name = imageUrl+".jpg";

        Picasso.with(context)
                .load(imageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {


                                  if (!imageDir.exists()) {
                                      imageDir.mkdirs();
                                  }

                                  File imageFile = new File(imageDir, name);

                                  FileOutputStream out = new FileOutputStream(imageFile);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                  imgDrawable[0] = Uri.fromFile(imageFile);

                                  out.flush();
                                  out.close();
                              } catch (Exception e) {
                                  // some action
                              }
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );


        return imgDrawable[0];


    }


    public Uri getImageFromStorage(final String imageUrl, String uid) {

        Uri imgDrawable = null;

        String[] uidextarr = ((imageUrl.split("attachments")[1]).split("_gallery")[0]).split("_");
        String uidext = uidextarr[uidextarr.length - 1];


        final String name = "roompic_" + uid + "_" + uidext + ".jpg";


        try {


            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            File imageFile = new File(imageDir, name);

            if (!imageFile.exists()) {

                imgDrawable = downloadProfileImage(imageUrl, uid);

            } else {


                imgDrawable = Uri.fromFile(imageFile);


            }


        } catch (Exception e) {
            // some action
        }


        return imgDrawable;
    }


    public Boolean isThatImageExist(final String imageUrl, String uid) {


        String[] uidextarr = ((imageUrl.split("attachments")[1]).split("_gallery")[0]).split("_");
        String uidext = uidextarr[uidextarr.length - 1];


        final String name = "roompic_" + uid + "_" + uidext + ".jpg";


        try {


            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            File imageFile = new File(imageDir, name);

            if (!imageFile.exists()) {

                return false;

            } else {
                return true;

            }


        } catch (Exception e) {
            // some action
        }


        return false;
    }



    public void downloadAllRoomPic(){

        ArrayList<ChatRoom> chatRooms = dbHelper.getAllRoom();

        for(ChatRoom croom : chatRooms){

            getImageFromStorage(croom.getPhotoUrl(),croom.getRoomId());

        }



    }





}
