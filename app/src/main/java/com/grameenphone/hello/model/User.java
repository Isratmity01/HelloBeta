package com.grameenphone.hello.model;


public class User {
    private String uid;
    public String phone;
    public String name;
    public String photoUrl;

    private int userpoint;


    public String firebaseToken;



    private int isMod;

    public User() {}

    public User(String uid, String name, String phone, String photoUrl) {
        this.uid = uid;
        this.phone = phone;
        this.name = name;
        this.photoUrl = photoUrl;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int isMod() {
        return isMod;
    }

    public void setMod(int mod) {
        isMod = mod;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }


    public int getUserpoint() {
        return userpoint;
    }

    public void setUserpoint(int userpoint) {
        this.userpoint = userpoint;
    }

}