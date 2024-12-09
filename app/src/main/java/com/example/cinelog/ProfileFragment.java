package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfileFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nickname = documentSnapshot.getString("nickname");
                        TextView nicknameTextView = view.findViewById(R.id.nickname);
                        nicknameTextView.setText(nickname);
                        Log.d("Firestore", "Nickname: " + nickname);
                    } else {
                        Log.d("Firestore", "Document does not exist!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching nickname: ", e);
                });

        db.collection("users").document(uid).collection("ratings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int ratingCount = task.getResult().size();
                        TextView ratingCountTextView = view.findViewById(R.id.rating_count);
                        ratingCountTextView.setText(String.valueOf(ratingCount));
                        Log.d("Firestore", "문서 개수: " + ratingCount);
                    } else {
                        Log.e("Firestore", "문서 개수 가져오기 실패: ", task.getException());
                    }
                });

        view.findViewById(R.id.rating_count).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RatingListActivity.class));
        });

        view.findViewById(R.id.keyword_settingsButton).setOnClickListener(v -> {
           startActivity(new Intent(getActivity(), KeywordSettingsActivity.class));
        });

        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LogInActivity.class));
            getActivity().finish();
        });

        view.findViewById(R.id.setting_button).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        Bundle nick = new Bundle();
        nick.putString("nickname",uid);
        getParentFragmentManager().setFragmentResult("requestkey",nick);


    }

}