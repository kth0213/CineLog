package com.example.cinelog;

public class Search_Movie {
    private String title;
    private String posterUrl;
    private String director;
    private String time;

    public Search_Movie(String title, String posterUrl, String director,String time) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.director = director;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTime(){ return time;}

}