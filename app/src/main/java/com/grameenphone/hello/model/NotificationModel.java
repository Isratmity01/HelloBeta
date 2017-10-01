package com.grameenphone.hello.model;



public class NotificationModel {


    private String sender, title, text;

    public NotificationModel() {
    }

    public NotificationModel(String sender, String title, String text) {
        this.sender = sender;
        this.title = title;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
