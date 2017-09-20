package com.grameenphone.hello.model;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by shadman.rahman on 11-Jun-17.
 */

public class SelectUser implements Comparable<SelectUser>{
    String name;
    String thumb;
    String phone;
    String uid;
    Boolean checkedBox = false;
    String email;
    private Boolean isHelloUser=false;

    public Boolean getHelloUser() {
        return isHelloUser;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setHelloUser(Boolean helloUser) {
        isHelloUser = helloUser;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public Boolean getCheckedBox() {
        return checkedBox;
    }

    public void setCheckedBox(Boolean checkedBox) {
        this.checkedBox = checkedBox;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static Comparator<SelectUser> NameComparator = new Comparator<SelectUser>() {

        public int compare(SelectUser s1, SelectUser s2) {
            String StudentName1 = s1.getName().toUpperCase();
            String StudentName2 = s2.getName().toUpperCase();

            //ascending order
            return StudentName1.compareTo(StudentName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

    @Override
    public int compareTo(@NonNull SelectUser o) {
        return name.compareTo(o.name);
    }
}