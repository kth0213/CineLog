package com.example.cinelog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Writing_Activity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText editTitle, editContent;
    private CheckBox checkBox;
    private Button button;
    private String nickname;
    private String profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        editTitle = findViewById(R.id.edit_title);
        editContent = findViewById(R.id.edit_content);
        checkBox = findViewById(R.id.checkbox);
        button = findViewById(R.id.button_submit_post);

        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nickname = documentSnapshot.getString("nickname");
                        profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    }
                })
                .addOnFailureListener(e -> Log.e("Error", "Failed to fetch nickname", e));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();
                boolean spoiler = checkBox.isChecked();
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(Writing_Activity.this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                savePostToFirestore(title, content, spoiler,nickname,profileImageUrl);

                finish();
            }
        });








    }
    private void savePostToFirestore(String title, String content, boolean isSpoiler, String nickname, String profileImageUrl) {

        String postId = db.collection("posts").document().getId(); // 고유 ID 생성
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("isSpoiler", isSpoiler); // 스포일러 여부 추가
        post.put("timestamp", FieldValue.serverTimestamp()); // 서버 시간 사용
        post.put("author",nickname);
        post.put("profileImageUrl", profileImageUrl);
        post.put("id",postId);

        db.collection("posts").document(postId)
                .set(post)

                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "게시물이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                    db.collection("users").document(mAuth.getUid()).update("postsCount", FieldValue.increment(1));
                    finish(); // 액티비티 종료
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "게시물 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}