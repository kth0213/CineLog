package com.example.cinelog;

public class Vote {
    private String title; // 투표 옵션 제목
    private String image; // 이미지 URL
    private int votes;    // 투표 수

    // 기본 생성자 (Firebase Firestore에서 객체를 자동으로 매핑하기 위해 필요)
    public Vote() {
    }

    // 생성자
    public Vote(String title, String image, int votes) {
        this.title = title;
        this.image = image;
        this.votes = votes;
    }

    // Getter와 Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
