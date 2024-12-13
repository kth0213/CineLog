package com.example.cinelog;

public class Movie {
    public static final int TYPE_YEAR_MONTH = 0; // 년/월 표시
    public static final int TYPE_MOVIE = 1; // 영화 기록

    private int viewType;
    private String title;
    private String posterPath; // Firebase Storage 경로
    private float rating;
    private String date;

    public Movie() {}

    public Movie(String title, String posterPath) {
        this.title = title;
        this.posterPath = posterPath;
    }
    public Movie(String title, String posterPath, Float rating) {
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
    }

    public Movie(int viewType, String date) {
        this.viewType = viewType;
        this.date = date;
    }

    public Movie(int viewType, String title, String posterPath, float rating, String date) {
        this.viewType = viewType;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.date = date;
    }

    public Movie(String title, String posterPath, Float rating, String date) {
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
        this.date = date;
    }

    public int getViewType() {
        return viewType;
    }
    public void setViewType(int viewType){
        this.viewType = viewType;
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
