package com.grameenphone.hello.Utils;



public class Userlevels {

    public static String getLevelName(int userpoint){

        if(userpoint < 50){
            return "লেভেল ১";
        } else if( userpoint < 120 ){
            return "লেভেল ২";
        } else if( userpoint < 400 ){
            return "লেভেল ৩";
        } else if( userpoint < 1600 ){
            return "লেভেল ৪";
        } else if( userpoint < 4000 ){
            return "লেভেল ৫";
        } else {
            return "লেভেল ১";
        }

    }


}
