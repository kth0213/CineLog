package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinelog.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private Button settingsButton, logoutButton;
    private ImageView keywordSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Views
        keywordSettingsButton = binding.keywordSettingsButton; // For keyword settings (ImageView)
        settingsButton = binding.settingsButton; // For app settings (Button)
        logoutButton = binding.logoutButton; // Logout button

        // User Profile
        TextView usernameTextView = binding.usernameTextView;
        ImageView profileImageView = binding.profileImageView;

        // Statistics
        TextView recordCountTextView = binding.recordCountTextView;
        TextView postsCountTextView = binding.postsCountTextView;
        TextView commentsCountTextView = binding.commentsCountTextView;

        // Sample User Data
        String username = "SoongsilKim1897";
        int recordCount = 256;
        int postsCount = 16;
        int commentsCount = 64;

        // Apply Data to UI
        usernameTextView.setText(username);
        recordCountTextView.setText(String.valueOf(recordCount));
        postsCountTextView.setText(String.valueOf(postsCount));
        commentsCountTextView.setText(String.valueOf(commentsCount));

        // Set Listeners
        // 1. Keyword Settings Button
        keywordSettingsButton.setOnClickListener(v -> {
            Intent keywordSettingsIntent = new Intent(ProfileActivity.this, KeywordSettingsActivity.class);
            startActivity(keywordSettingsIntent);
        });

        // 2. Settings Button
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        // 3. Logout Button
        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(ProfileActivity.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}




