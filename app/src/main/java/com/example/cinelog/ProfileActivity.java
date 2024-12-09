package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinelog.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private Button settingsButton, logoutButton;
    private ImageView keywordSettingsButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // UI 바인딩
        keywordSettingsButton = binding.keywordSettingsButton;
        settingsButton = binding.settingsButton;
        logoutButton = binding.logoutButton;

        TextView usernameTextView = binding.usernameTextView;
        ImageView profileImageView = binding.profileImageView;
        TextView recordCountTextView = binding.recordCountTextView;
        TextView postsCountTextView = binding.postsCountTextView;
        TextView commentsCountTextView = binding.commentsCountTextView;


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // 사용자 이름 및 데이터 가져오기
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 사용자 데이터 가져오기
                        String username = document.getString("username");
                        long recordCount = document.getLong("recordCount");
                        long postsCount = document.getLong("postsCount");
                        long commentsCount = document.getLong("commentsCount");

                        // 데이터 설정
                        usernameTextView.setText(username);
                        recordCountTextView.setText(String.valueOf(recordCount));
                        postsCountTextView.setText(String.valueOf(postsCount));
                        commentsCountTextView.setText(String.valueOf(commentsCount));
                    }
                } else {
                    // 데이터 가져오기 실패 처리
                    usernameTextView.setText("실패");
                }
            });
        } else {

            usernameTextView.setText("Guest");
        }


        keywordSettingsButton.setOnClickListener(v -> {
            Intent keywordSettingsIntent = new Intent(ProfileActivity.this, KeywordSettingsActivity.class);
            startActivity(keywordSettingsIntent);
        });


        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });


        logoutButton.setOnClickListener(v -> {
            mAuth.signOut(); // Firebase 로그아웃
            Intent loginIntent = new Intent(ProfileActivity.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}





