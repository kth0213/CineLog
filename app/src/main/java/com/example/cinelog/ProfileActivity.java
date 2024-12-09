package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinelog.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
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

        keywordSettingsButton = binding.keywordSettingsButton;
        settingsButton = binding.settingsButton;
        logoutButton = binding.logoutButton;

        TextView usernameTextView = binding.usernameTextView;
        ImageView profileImageView = binding.profileImageView;

        TextView recordCountTextView = binding.recordCountTextView;
        TextView postsCountTextView = binding.postsCountTextView;
        TextView commentsCountTextView = binding.commentsCountTextView;

        String username = "SoongsilKim1897";
        int recordCount = 256;
        int postsCount = 16;
        int commentsCount = 64;

        usernameTextView.setText(username);
        recordCountTextView.setText(String.valueOf(recordCount));
        postsCountTextView.setText(String.valueOf(postsCount));
        commentsCountTextView.setText(String.valueOf(commentsCount));

        keywordSettingsButton.setOnClickListener(v -> {
            Intent keywordSettingsIntent = new Intent(ProfileActivity.this, KeywordSettingsActivity.class);
            startActivity(keywordSettingsIntent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(ProfileActivity.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}




