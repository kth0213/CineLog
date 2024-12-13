package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ImageButton;
import android.content.SharedPreferences;


import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    private Button manageAccountButton;
    private ImageButton backButton; // Back Button 추가
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        notificationSwitch = findViewById(R.id.notificationSwitch);
        manageAccountButton = findViewById(R.id.manageAccountButton);
        backButton = findViewById(R.id.back_button); // Back Button 초기화


        backButton.setOnClickListener(v -> {
            finish();
        });


        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "알림 활성화됨" : "알림 비활성화됨", Toast.LENGTH_SHORT).show();
        });


        manageAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });


    }
}



