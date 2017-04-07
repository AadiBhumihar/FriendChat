package com.example.bhumihar.friendchat;

import java.util.Date;

/**
 * Created by bhumihar on 28/3/17.
 */

public class ChatMessage {

    private String uid ;
    private String messageText;
    private String imagebitmap ;
    private String messageUser;
    private long messageTime;


    public ChatMessage(String uid , String messageText, String messageUser) {
        this.uid = uid ;
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(String uid ,String messageText, String imagebitmap ,String messageUser) {
        this.uid = uid ;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.imagebitmap = imagebitmap ;
        messageTime = new Date().getTime();

    }

    public ChatMessage(String uid, String messageUser) {
        this.uid = uid;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getUid() {
        return uid;
    }

    public String getImagebitmap() {
        return imagebitmap;
    }

    public void setImagebitmap(String imagebitmap) {

        this.imagebitmap = imagebitmap;
    }
}
