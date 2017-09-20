package com.grameenphone.hello.model;



public class ChatRoom {

    private String roomId;
    private String name;
    private String photoUrl;
    private int requestStatus;

    private Chat lastChat;
    private String unreadMessageCount;





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


    public Chat getLastChat() {
        return lastChat;
    }

    public void setLastChat(Chat lastChat) {
        this.lastChat = lastChat;
    }

    public String getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(String unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }
}
