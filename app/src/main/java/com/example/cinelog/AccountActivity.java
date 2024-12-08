package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView userEmailTextView;
    private TextView userNameTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        userEmailTextView = findViewById(R.id.userEmailTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        logoutButton = findViewById(R.id.logoutButton);


        if (currentUser != null) {
            userEmailTextView.setText(currentUser.getEmail());
            userNameTextView.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous");
        }


        logoutButton.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Intent loginIntent = new Intent(AccountActivity.this, LogInActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}
