package com.grameenphone.hello.Adapter.LiveRoomViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.hello.R;


public class LiveStickerHolder extends RecyclerView.ViewHolder {

    public ImageView liveSticker;
    public TextView timestamp;
    public ImageView Badge;
    public TextView pName;

    public LiveStickerHolder(View itemView) {
        super(itemView);
        liveSticker = (ImageView) itemView.findViewById(R.id.livesticker);
        timestamp = (TextView) itemView.findViewById(R.id.time_stamp_live);
        pName = (TextView) itemView.findViewById(R.id.liveusernamestk);
        Badge = (ImageView) itemView.findViewById(R.id.badgestk);


    }

    public ImageView getLiveSticker() {
        return liveSticker;
    }

    public void setLiveSticker(ImageView liveSticker) {
        this.liveSticker = liveSticker;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
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
