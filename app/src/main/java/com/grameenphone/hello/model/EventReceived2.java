package com.grameenphone.hello.model;

/**
 * Created by HP on 9/18/2017.
 */

public class EventReceived2 {

    private boolean isSuccessful;

    private String responseMessage;

    public EventReceived2(boolean success, String message){
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