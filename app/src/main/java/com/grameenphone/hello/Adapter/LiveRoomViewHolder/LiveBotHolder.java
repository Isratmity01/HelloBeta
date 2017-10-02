package com.grameenphone.hello.Adapter.LiveRoomViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.hello.R;

import github.ankushsachdeva.emojicon.EmojiconTextView;


public class LiveBotHolder extends RecyclerView.ViewHolder {

    public EmojiconTextView liveText;
    public TextView timestamp;
    public ImageView Badge;
    public TextView pName;

    public LiveBotHolder(View itemView) {
        super(itemView);
        liveText = (EmojiconTextView) itemView.findViewById(R.id.live_messagebot);
        timestamp = (TextView) itemView.findViewById(R.id.time_stamp_live);
        pName = (TextView) itemView.findViewById(R.id.liveusernamebot);
        Badge = (ImageView) itemView.findViewById(R.id.badgebot);


    }

    public EmojiconTextView getLiveText() {
        return liveText;
    }

    public void setLiveText(EmojiconTextView liveText) {
        this.liveText = liveText;
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
