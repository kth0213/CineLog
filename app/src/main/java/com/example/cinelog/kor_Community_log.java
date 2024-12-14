package com.example.cinelog;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class kor_Community_log extends AppCompatActivity {

    private List<Comment> commentList;
    private EditText commentEditText;
    private CommentsAdapter commentsAdapter;
    private String postid;
    private String timestamp;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kor_community_log);

        db = FirebaseFirestore.getInstance();

        ImageView back_but = findViewById(R.id.back_button2);

        back_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleText = findViewById(R.id.log_title);
        TextView contentText = findViewById(R.id.log_content);
        TextView authorText = findViewById(R.id.log_author);
        TextView timeText = findViewById(R.id.log_time);

        ImageView author_image = findViewById(R.id.author_image);

        // kor_Comunity에서 준 인텐트로 내용 가져오기
        Intent intent = getIntent();

//        String title = intent.getStringExtra("title");
//        String cotent = intent.getStringExtra("content");
//        String author = intent.getStringExtra("author");
//        String profileUrl = intent.getStringExtra("profileUrl");
//        Log.d("kor_Community_log", "Profile URL received: " + profileUrl);
//        timestamp = intent.getStringExtra("timestamp");
//
//
//        // 저장할 게시물 id 따로 받기
        postid = intent.getStringExtra("id");
//
//
//        //내용 설정
//        titleText.setText(title);
//        contentText.setText(cotent);
//        authorText.setText(author);
//        timeText.setText(timestamp);
//
//        if (profileUrl != null && !profileUrl.isEmpty()) {
//            Glide.with(this)
//                    .load(profileUrl) // Firestore에 저장된 URL
//                    .placeholder(R.drawable.rounded_image) // 로딩 중 기본 이미지
//                    .error(R.drawable.baseline_block_24) // 오류 발생 시 이미지
//                    .circleCrop() // 둥근 이미지로 자르기
//                    .into(author_image); // ImageView에 로드
//        } else {
//            author_image.setImageResource(R.drawable.baseline_calendar_today_24); // URL이 없을 경우 기본 이미지
//        }
        db.collection("posts").document(postid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 게시물 데이터 설정
                        String title = documentSnapshot.getString("title");
                        String content = documentSnapshot.getString("content");
                        String author = documentSnapshot.getString("author");
                        String profileUrl = documentSnapshot.getString("profileImageUrl");
                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");

                        titleText.setText(title);
                        contentText.setText(content);
                        authorText.setText(author);

                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault());
                            timeText.setText(dateFormat.format(date));
                        }

                        // 프로필 이미지 설정
                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileUrl)
                                    .placeholder(R.drawable.rounded_image) // 로딩 중 기본 이미지
                                    .error(R.drawable.baseline_block_24) // 오류 발생 시 기본 이미지
                                    .circleCrop() // 둥글게 자르기
                                    .into(author_image);
                        } else {
                            author_image.setImageResource(R.drawable.baseline_calendar_today_24); // 기본 이미지
                        }
                    } else {
                        Toast.makeText(this, "게시물 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "게시물을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    finish();
                });
















        // 댓글 리사이클러뷰 설정
        RecyclerView commentsRecyclerView = findViewById(R.id.recyclerview_log);

        commentEditText = findViewById(R.id.comment);

        // 댓글 리스트 초기화 및 리사이클러뷰 설정

        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        loadComments();

        Button send_but = findViewById(R.id.send_but);

        send_but.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(commentText);
            }
        });

        TextView report = findViewById(R.id.report);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String postId = getIntent().getStringExtra("id"); // 게시물 ID
                if (postId != null) {
                    reportPost(postId);
                }
            }
        });

    }

    //댓글 입력 어뎁터 생성
    public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

        private List<Comment> commentList;


        public CommentsAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.commentText.setText(comment.getText());



        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public  class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView commentText, timestampText;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                commentText = itemView.findViewById(R.id.comment_context);
            }
        }
    }

    private void loadComments() {

        db.collection("posts").document(postid).collection("comments")
                    .orderBy("timestamp", Query.Direction.ASCENDING) // 시간순 정렬
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            error.printStackTrace();
                            return;
                        }

                        commentList.clear();
                        if (value != null) {
                            for (DocumentSnapshot doc : value) {
                                Comment comment = doc.toObject(Comment.class);
                                commentList.add(comment);
                            }
                        }
                        commentsAdapter.notifyDataSetChanged();
                    });
        }

    private void addComment(String text) {

            Map<String, Object> comment = new HashMap<>();
            comment.put("text", text);
            comment.put("timestamp", FieldValue.serverTimestamp());

            db.collection("posts").document(postid).collection("comments")
                    .add(comment)
                    .addOnSuccessListener(documentReference -> {
                        commentEditText.setText(""); // 입력창 초기화
                        db.collection("posts").document(postid).update("commentCount", FieldValue.increment(1));
                        Toast.makeText(this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "댓글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        }



    // 신고 메서드
    private void reportPost(String postId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // `reports` 컬렉션에서 해당 게시물 ID를 찾음
        db.collection("reports").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 이미 신고 데이터가 존재할 경우
                        List<String> users = (List<String>) documentSnapshot.get("users");
                        if (users != null && users.contains(currentUserId)) {
                            // 이미 신고한 사용자라면
                            Toast.makeText(this, "이미 신고했습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 신고한 적 없는 사용자라면
                            incrementReportCount(postId, currentUserId, users);
                        }
                    } else {
                        // 신고 데이터가 없는 경우 새로운 문서 생성
                        createNewReport(postId, currentUserId);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "신고 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void incrementReportCount(String postId, String currentUserId, List<String> existingUsers) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 기존 사용자 리스트에 현재 사용자 추가
        if (existingUsers == null) existingUsers = new ArrayList<>();
        existingUsers.add(currentUserId);

        // 신고 횟수와 사용자 리스트 업데이트
        db.collection("reports").document(postId)
                .update("count", FieldValue.increment(1), "users", existingUsers)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "신고했습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "신고 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewReport(String postId, String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 새로운 신고 데이터 생성
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("count", 1); // 첫 번째 신고
        reportData.put("users", Collections.singletonList(currentUserId)); // 신고 사용자 리스트

        db.collection("reports").document(postId)
                .set(reportData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "신고했습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "신고 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

}