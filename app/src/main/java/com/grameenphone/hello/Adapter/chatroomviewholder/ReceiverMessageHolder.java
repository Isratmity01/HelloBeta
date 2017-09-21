package com.grameenphone.hello.Adapter.chatroomviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grameenphone.hello.R;


public class ReceiverMessageHolder extends RecyclerView.ViewHolder {


    public TextView receiversMessage;
    public TextView timestamp;

    public ReceiverMessageHolder(View itemView) {
        super(itemView);
        receiversMessage = (TextView) itemView.findViewById(R.id.receiversMessage);
        timestamp = (TextView) itemView.findViewById(R.id.receiversmsgName);
    }



    public TextView getReceiversMessage() {
        return receiversMessage;
    }

    public void setReceiversMessage(TextView receiversMessage) {
        this.receiversMessage = receiversMessage;
    }

    public TextView getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(TextView timestamp) {
        this.timestamp = timestamp;
    }

}
