package com.chat.tntchatapp.Models;

public class Users {
    String id,userName,imageUrl,email,status,search;

    public Users(String email, String id, String imageUrl, String userName,String status,String search) {
        this.id = id;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.email = email;
        this.status=status;
        this.search=search;
    }

    public Users() {
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch() {
        return search;
    }
}