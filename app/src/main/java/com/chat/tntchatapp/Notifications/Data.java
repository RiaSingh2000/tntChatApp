package com.chat.tntchatapp.Notifications;

public class Data {
    String user,body,title,sented;
    int icon;

    public Data(String user, String body, String title, String sented, int icon) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.icon = icon;
    }
    public Data() {
    }


    public String getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public String getSented() {
        return sented;
    }

    public int getIcon() {
        return icon;
    }


}
