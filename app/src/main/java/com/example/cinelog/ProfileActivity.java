package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinelog.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 사용자 정보 표시
        TextView usernameTextView = binding.usernameTextView;
        ImageView profileImageView = binding.profileImageView;

        // 기록, 글, 댓글 수 텍스트뷰
        TextView recordCountTextView = binding.recordCountTextView;
        TextView postsCountTextView = binding.postsCountTextView;
        TextView commentsCountTextView = binding.commentsCountTextView;

        // 샘플 사용자 데이터
        String username = "SoongsilKim1897";
        int recordCount = 256;
        int postsCount = 16;
        int commentsCount = 64;

        // 데이터 적용
        usernameTextView.setText(username);
        recordCountTextView.setText(String.valueOf(recordCount));
        postsCountTextView.setText(String.valueOf(postsCount));
        commentsCountTextView.setText(String.valueOf(commentsCount));

        // 설정 버튼 클릭 이벤트
        Button settingsButton = binding.settingsButton;
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        // 로그아웃 버튼 클릭 이벤트
        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(ProfileActivity.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish(); // 프로필 화면 종료
        });
    }
}
