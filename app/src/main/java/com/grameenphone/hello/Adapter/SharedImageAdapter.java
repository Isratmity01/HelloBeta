package com.grameenphone.hello.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.BorderTransform;
import com.grameenphone.hello.Utils.ImageDialog;

import java.util.ArrayList;

/**
 * Created by HP on 8/27/2017.
 */

public class SharedImageAdapter extends BaseAdapter {
    private Activity mContext;
    View view;
    Bitmap res;
    // Keep all Images in array
    private ArrayList<String> images=new ArrayList<>();

   private Boolean flag_selected=false;

    // Constructor
    public SharedImageAdapter(Activity mainActivity, ArrayList<String> items) {
        this.mContext = mainActivity;
        this.images = items;

        flag_selected=true;
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setSelected_position(int position)
    {
        //selected_position=position;
        notifyDataSetChanged();
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image_item, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.sharedimage);
        String path=images.get(position);
            Glide.with(mContext).load(path).placeholder(R.drawable.ic_user_pic_02).into(imageView);;
    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String drawable=images.get(position);
            ImageDialog imageDialog=new ImageDialog(mContext);
            // imageDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            imageDialog.start(mContext, drawable,"no");
        }
    });
        return imageView;
    }
}

