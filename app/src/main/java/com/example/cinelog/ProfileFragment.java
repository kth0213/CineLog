package com.example.cinelog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView profileImage;
    private TextView nickname, ratingCount, writingCount, commentCount;
    private Button keywordSettingsButton, logoutButton;
    private ImageButton settingButton;

    public ProfileFragment() {
    }

    public static Fragment newInstance(String param1, String param2)
    {

        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        nickname = view.findViewById(R.id.nickname);
        ratingCount = view.findViewById(R.id.rating_count);
        writingCount = view.findViewById(R.id.writing_count);
        commentCount = view.findViewById(R.id.comment_count);
        keywordSettingsButton = view.findViewById(R.id.keyword_settingsButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        settingButton = view.findViewById(R.id.setting_button);
        profileImage = view.findViewById(R.id.profileImage);


        loadUserData();
        loadUserStatistics();


        keywordSettingsButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), KeywordSettingsActivity.class)));


        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LogInActivity.class));
            getActivity().finish();
        });

        settingButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });


    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // 닉네임 설정
                String userNickname = documentSnapshot.getString("nickname");
                nickname.setText(userNickname != null ? userNickname : "Anonymous");

                // 프로필 이미지 로드
                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
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

    private void loadUserStatistics() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long ratings = documentSnapshot.getLong("ratingsCount");
                long posts = documentSnapshot.getLong("postsCount");
                long comments = documentSnapshot.getLong("commentsCount");

                ratingCount.setText(String.valueOf(ratings));
                writingCount.setText(String.valueOf(posts));
                commentCount.setText(String.valueOf(comments));
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "통계 가져오기 오류: ", e));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
        }
    }

}