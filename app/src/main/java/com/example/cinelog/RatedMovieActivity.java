package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cinelog.databinding.ActivityRatedMovieBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RatedMovieActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRatedMovieBinding binding = ActivityRatedMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String posterPath = intent.getStringExtra("posterPath");
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("ratings")
                .whereEqualTo("title", title) // "title" 필드가 특정 값과 같은 문서 검색
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fetchedTitle = document.getString("title");

                            if (fetchedTitle != null) {
                                Log.d("Firestore", "Movie found: " + fetchedTitle);
                                binding.title.setText(fetchedTitle);
                                Glide.with(binding.poster.getContext())
                                        .load(document.getString("posterPath"))
                                        .into(binding.poster);
                                binding.comment.setText(document.getString("comment"));
                                binding.date.setText(document.getString("date"));
                                binding.place.setText(document.getString("place"));
                                binding.friend.setText(document.getString("friend"));
                                binding.memo.setText(document.getString("memo"));
                                binding.ratingbar.setRating(document.getLong("rating"));
                            }

                        }

                        if (task.getResult().isEmpty()) {
                            Log.d("Firestore", "No movie found with title: " + title);
                        }
                    } else {
                        Log.e("Firestore", "Error fetching movie by title: ", task.getException());
                    }
                });
        ImageButton editButton = binding.editButton;
        editButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(v.getContext(), RatingActivity.class);
            intent1.putExtra("title",title);
            intent1.putExtra("posterUrl",posterPath);
            startActivity(intent1);
        });
        binding.backButton.setOnClickListener(view -> {
            finish();
        });
    }

}