package com.example.cinelog;

public class Keyword {
    private String keyword;
    private int count;

    // Required no-argument constructor for Firestore
    public Keyword() {}

    public Keyword(String keyword, int count) {
        this.keyword = keyword;
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

