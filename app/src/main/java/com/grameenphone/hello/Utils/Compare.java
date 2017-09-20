package com.grameenphone.hello.Utils;

/**
 * Created by HP on 8/31/2017.
 */
public class Compare {

    public static String getRoomName(String sender, String receiver){
        String chatroom = "";
        int compare = sender.compareTo(receiver);
        if(compare < 0){
            chatroom = sender+"_"+receiver;
        }
        else chatroom = receiver+"_"+sender;

        return chatroom;
    }
}
