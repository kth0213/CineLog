package com.example.cinelog;

import com.google.firebase.Timestamp;

public class Comment {

    private String text;
    private String author;
    private Timestamp timestamp;

    public Comment() {}

    public Comment(String text, String author, Timestamp timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }


    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
