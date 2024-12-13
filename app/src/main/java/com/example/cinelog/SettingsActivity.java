package com.example.cinelog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // 이미지 선택 요청 코드

    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    private Button manageAccountButton;
    private ImageButton editNickname;
    private ImageButton backButton; // Back Button 추가
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private BottomSheetDialog dialog;
    private TextView nickname;
    private ImageView profileImage;

    private Uri selectedImageUri; // 선택한 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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

        // 프로필 이미지 클릭 리스너 (이미지 선택)
        profileImage.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
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
                String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                // 닉네임 설정
                nickname.setText(userNickname != null ? userNickname : "Anonymous");

                // 프로필 이미지 로드
                if (profileImageUrl != null) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .circleCrop() // 이미지를 둥글게 처리
                            .placeholder(R.drawable.rounded_image) // 로딩 중 보여줄 기본 이미지 (옵션)
                            .into(profileImage);
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "사용자 데이터 가져오기 오류: ", e));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                // 이미지 미리보기
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImage.setImageBitmap(bitmap);

                // Firestore와 Firebase Storage에 저장
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (selectedImageUri != null) {
            String uid = mAuth.getCurrentUser().getUid();
            StorageReference userImageRef = storageRef.child("users/" + uid + "/profile.jpg");

            userImageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> userImageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Firestore에 이미지 URL 저장
                                db.collection("users").document(uid)
                                        .update("profileImageUrl", uri.toString())
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "프로필 이미지가 업데이트되었습니다.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this, "프로필 이미지 URL 저장에 실패했습니다.", Toast.LENGTH_SHORT).show());
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }
}