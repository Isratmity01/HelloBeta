package com.grameenphone.hello.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.BorderTransform;
import com.grameenphone.hello.Utils.CircularTransform;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by HP on 8/27/2017.
 */

public class AvatarAdapter extends BaseAdapter {
    private Activity mContext;
    View view;
    // Keep all Images in array
    public Integer[] mThumbIds;
    ImageView imageView;
    int selected_position;
   private Boolean flag_selected=false;

    // Constructor
    public AvatarAdapter(Activity mainActivity, Integer[] items,int number) {
        this.mContext = mainActivity;
        this.mThumbIds = items;
        this.selected_position=number;
        flag_selected=true;
    }
    public AvatarAdapter(Activity mainActivity, Integer[] items) {
        this.mContext = mainActivity;
        this.mThumbIds = items;

    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setSelected_position(int position)
    {
        selected_position=position;
        notifyDataSetChanged();
    }
    public View getView(int position, View convertView, ViewGroup parent) {
         view = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_item, null);
         imageView = (ImageView) view.findViewById(R.id.avatar);

        if(position==selected_position && flag_selected)
        {
            Glide.with(mContext).load(mThumbIds[position]).transform(new BorderTransform(mContext))
            .into(imageView);

        }
else {
            Glide.with(mContext).load(mThumbIds[position]).bitmapTransform(new CircularTransform(mContext)).placeholder(R.drawable.ic_user_pic_02).into(imageView);
        }
        return imageView;
    }
}

