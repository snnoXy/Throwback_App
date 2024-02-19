package com.example.throwback.object;

import java.util.UUID;

public class Throwback {

    public String email;
    public String description;
    public String downloadUrl;
    public String title;
    public String date;
    public String uid;
    public Throwback(String email, String description, String downloadUrl, String title, String date, String uid) {
        this.email = email;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.title = title;
        this.date = date;
        this.uid = uid;
    }
}
