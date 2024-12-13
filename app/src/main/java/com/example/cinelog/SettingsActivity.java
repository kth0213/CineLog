package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.content.SharedPreferences;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    private Button manageAccountButton;
    private ImageButton editNickname;
    private ImageButton backButton; // Back Button 추가
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private BottomSheetDialog dialog;
    private TextView nickname;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        notificationSwitch = findViewById(R.id.notificationSwitch);
        manageAccountButton = findViewById(R.id.manageAccountButton);
        backButton = findViewById(R.id.back_button); // Back Button 초기화
        editNickname = findViewById(R.id.edit_nickname_button);
        nickname = findViewById(R.id.edit_nickname);
        profileImage = findViewById(R.id.edit_profileImage);

        loadUserData();

        dialog = new BottomSheetDialog(this);
        editNickname.setOnClickListener(view -> {
            View contentView = SettingsActivity.this.getLayoutInflater().inflate(R.layout.fragment_bottom_sheet, null);
            dialog.setContentView(contentView);

            Button okButton = contentView.findViewById(R.id.ok_button);
            okButton.setOnClickListener(view1 -> {
               TextView editText = contentView.findViewById(R.id.edit_nickname_text);
                nickname.setText(editText.getText().toString());
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                                .update("nickname", editText.getText().toString());
                dialog.dismiss();
            });
            dialog.show();
        });

        profileImage.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        });



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
    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userNickname = documentSnapshot.getString("nickname");
                nickname.setText(userNickname != null ? userNickname : "Anonymous");
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "사용자 데이터 가져오기 오류: ", e));
    }
}



