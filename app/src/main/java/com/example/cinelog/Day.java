package com.example.cinelog;

public class Day {
    private final int date;
    private final String posterUrl;

    public Day(int date, String posterUrl) {
        this.date = date;
        this.posterUrl = posterUrl;
    }

    public int getDate() {
        return date;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
