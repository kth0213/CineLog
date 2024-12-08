package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountManagementActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private Button changePasswordButton;
    private Button deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);


        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);


        String username = "SoongsilKim1897";
        String email = "soongsilkim@example.com";


        usernameTextView.setText(username);
        emailTextView.setText(email);

        // Change Password Button
//        changePasswordButton.setOnClickListener(v -> {
//            // Navigate to a hypothetical password change screen
//            Intent intent = new Intent(AccountManagementActivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//        });


        deleteAccountButton.setOnClickListener(v -> {
            // Placeholder for delete account logic
            Toast.makeText(this, "계정이 삭제되었습니다. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
            // Log the user out and navigate to the main activity
            Intent intent = new Intent(AccountManagementActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}


