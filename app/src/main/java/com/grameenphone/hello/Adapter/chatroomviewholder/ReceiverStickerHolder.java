package com.grameenphone.hello.Adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.hello.R;


public class ReceiverStickerHolder extends RecyclerView.ViewHolder {

    public ImageView sticker;
    public TextView timestamp;

    public ReceiverStickerHolder(View itemView) {
        super(itemView);
        sticker = (ImageView) itemView.findViewById(R.id.stickerimage_recevier);
        timestamp = (TextView) itemView.findViewById(R.id.receiversstkName);
    }


    public ImageView getReceiversMessage() {
        return sticker;
    }

    public void setReceiversMessage(ImageView receiversMessage) {
        this.sticker = receiversMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }

}
