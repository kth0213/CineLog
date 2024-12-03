package com.example.cinelog;

public class Movie {
    private String title;
    private String posterPath; // Firebase Storage 경로
    private float rating;
    private String date;

    public Movie() {}

    public Movie(String title, String posterPath) {
        this.title = title;
        this.posterPath = posterPath;
    }

    public Movie(String title, String posterPath, Float rating, String date) {
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate(){return date;}

    public void setDate(String date) {this.date = date;}
}
