package com.example.cinelog;

public class AppItem {
    private String imageUrl; // 앱 로고 이미지 URL
    private String packageName; // 앱 패키지 이름

    public AppItem(String imageUrl, String packageName) {
        this.imageUrl = imageUrl;
        this.packageName = packageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPackageName() {
        return packageName;
    }
}

