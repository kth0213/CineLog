package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinelog.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        EditText emailEditText = binding.email;
        EditText passwordEditText = binding.password;
        EditText passwordCheckEditText = binding.passwordCheck;

        TextView signUpButton = binding.signUp;
        signUpButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String passwordCheck = passwordCheckEditText.getText().toString();

            if(password.equals(passwordCheck)){
                signUp(email, password);
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if(user != null) {
            // 유저 데이터 저장
            String uid = user.getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", user.getEmail());
            userData.put("createdAt", System.currentTimeMillis());
            userData.put("nickname", user.getEmail());
            userData.put("commentsCount", 0);
            userData.put("postsCount",0);
            userData.put("ratingsCount", 0);

            // Firestore에 사용자 데이터 저장
            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "User data successfully written!");
                        // Firestore에 데이터 저장 완료 후 로그아웃 처리
                        mAuth.signOut();
                        // 로그인 화면으로 이동
                        Intent intent = new Intent(this, LogInActivity.class);
                        Toast.makeText(this, "SignUp has been completed.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error writing user data", e);
                        Toast.makeText(SignUpActivity.this, "Error during sign up", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}