package com.chat.tntchatapp.Models;

public class Chats {
    String receiver,sender,message, image_uri;

    public Chats(String receiver, String sender, String message,String image_uri) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.image_uri =image_uri;
    }

    public Chats() {
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
