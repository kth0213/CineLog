package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountManagementActivity extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView emailTextView;
    private Button changePasswordButton;
    private Button deleteAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        //changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);


        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userNickname = documentSnapshot.getString("nickname");
                String userEmail = documentSnapshot.getString("email");

                // 닉네임 설정
                usernameTextView.setText(userNickname);
                emailTextView.setText(userEmail);

            }
        }).addOnFailureListener(e -> Log.e("Firestore", "사용자 데이터 가져오기 오류: ", e));



        // Change Password Button
//        changePasswordButton.setOnClickListener(v -> {
//            // Navigate to a hypothetical password change screen
//            Intent intent = new Intent(AccountManagementActivity.this, ChangePasswordActivity.class);
//            startActivity(intent);
//        });


        deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AccountManagementActivity.this)
                    .setTitle("삭제 확인")
                    .setPositiveButton("예",(dialog, which) -> {
                        mAuth.getCurrentUser().delete();
                        Toast.makeText(this, "계정이 삭제되었습니다. 다시 시작하세요.", Toast.LENGTH_SHORT).show();
                        // Log the user out and navigate to the main activity
                        Intent intent = new Intent(AccountManagementActivity.this, LogInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("아니오",(dialog,which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }
}


