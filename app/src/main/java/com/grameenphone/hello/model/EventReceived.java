package com.grameenphone.hello.model;

import java.util.ArrayList;

/**
 * Created by HP on 9/18/2017.
 */

public class EventReceived{

    private boolean isSuccessful;

    private String responseMessage;

    public EventReceived(boolean success, String message){
        this.isSuccessful = success;
        this.responseMessage = message;
    }

    public boolean isLoginSuccessful(){
        return isSuccessful;
    }

    public String getResponseMessage(){
        return responseMessage;
    }

      /* You can add setters here if you want */

}