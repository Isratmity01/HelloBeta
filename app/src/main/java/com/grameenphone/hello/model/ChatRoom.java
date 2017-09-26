package com.grameenphone.hello.model;



public class ChatRoom {

    private String roomId;
    private String name;
    private String photoUrl;
    private int requestStatus;

    private String lastChat;
    private long timestamp;





    public ChatRoom() {
    }

    public ChatRoom(String roomId, String name, String photoUrl, int requestStatus) {
        this.roomId = roomId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.requestStatus = requestStatus;
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
