package com.example.cinelog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    private Button manageAccountButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        setTheme(isDarkMode ? R.style.Theme_Cinelog_Dark : R.style.Theme_Cinelog_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        manageAccountButton = findViewById(R.id.manageAccountButton);


        darkModeSwitch.setChecked(isDarkMode);


        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();


            Toast.makeText(this, "테마가 변경되었습니다. 앱을 다시 시작하세요.", Toast.LENGTH_SHORT).show();
            recreate(); // Optional: Apply changes immediately by recreating the activity
        });


        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "알림 활성화됨" : "알림 비활성화됨", Toast.LENGTH_SHORT).show();
        });


        manageAccountButton.setOnClickListener(v -> {
            // Navigate to the Account Management screen (placeholder)
            Intent intent = new Intent(SettingsActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });
    }
}

