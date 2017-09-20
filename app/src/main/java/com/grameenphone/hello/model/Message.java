package com.grameenphone.hello.model;

import java.io.Serializable;

public class Message implements Serializable {
    String username,photo, caption,created_at;
    int total_comment;

    public Message(String username, String photo, String caption, String created_at, int total_comment){
        this.username = username;
        this.photo = photo;
        this.caption = caption;
        this.created_at =created_at;
        this.total_comment =total_comment;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

}
