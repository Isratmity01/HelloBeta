package com.grameenphone.hello.Adapter.LiveRoomViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.grameenphone.hello.R;


public class LiveImageHolder extends RecyclerView.ViewHolder {

    public ImageView liveImage;
    public TextView timestamp;
    public ImageView downloadImage;
    public ImageView Badge;
    public TextView pName;

    public LiveImageHolder(View itemView) {
        super(itemView);
        liveImage = (ImageView) itemView.findViewById(R.id.liveimage);
        timestamp = (TextView) itemView.findViewById(R.id.time_stamp_live);
        pName = (TextView) itemView.findViewById(R.id.liveusernameimg);
        Badge = (ImageView) itemView.findViewById(R.id.badgeimg);
        downloadImage=(ImageView)itemView.findViewById(R.id.livedownloader);


    }


    public ImageView getLiveImage() {
        return liveImage;
    }

    public void setLiveImage(ImageView liveImage) {
        this.liveImage = liveImage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }

    public ImageView getDownloadImage() {
        return downloadImage;
    }

    public void setDownloadImage(ImageView downloadImage) {
        this.downloadImage = downloadImage;
    }

    public ImageView getBadge() {
        return Badge;
    }

    public void setBadge(ImageView badge) {
        Badge = badge;
    }

    public TextView getpName() {
        return pName;
    }

    public void setpName(TextView pName) {
        this.pName = pName;
    }
}
