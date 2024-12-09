package com.example.cinelog;


import com.google.firebase.Timestamp;

public class Post {
    private String id;
    private String title;     // 게시물 제목
    private String content;   // 게시물 내용
    private boolean isSpoiler; // 스포일러 여부
    private Timestamp timestamp; // 작성 시간 (ISO 8601 형식)
    private String author;    // 작성자 ID (선택 사항)


    // Firestore에서 객체를 생성하기 위해 기본 생성자가 필요함
    public Post() {}

    // 모든 필드를 포함하는 생성자
    public Post(String title, String content, boolean isSpoiler, Timestamp timestamp, String author) {
        this.title = title;
        this.content = content;
        this.isSpoiler = isSpoiler;
        this.timestamp = timestamp;
        this.author = author;
    }

    // Getter와 Setter
    public String getTitle() {
        return title;
    }
    public String getId(){
        return id;
    }

    public void  setId(String id){
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSpoiler() {
        return isSpoiler;
    }

    public void setSpoiler(boolean spoiler) {
        isSpoiler = spoiler;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
