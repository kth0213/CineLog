package com.example.cinelog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinelog.databinding.ActivityRatingBinding;
import com.example.cinelog.databinding.ActivityRatingListBinding;
import com.example.cinelog.databinding.ItemMovieBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class RatingListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Movie> movieList = new ArrayList<>();
    private RatingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRatingListBinding binding = ActivityRatingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // RecyclerView 설정
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 어댑터를 RecyclerView에 설정 (초기화가 필요함)
        adapter = new RatingAdapter(movieList);
        binding.recyclerView.setAdapter(adapter);

        // 영화 데이터 가져오기
        fetchMovies();
    }

    private void fetchMovies() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("ratings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 데이터가 성공적으로 불러와졌을 때 movieList를 업데이트
                        movieList.clear();  // 이전 데이터를 삭제
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String posterPath = document.getString("posterPath");

                            if (title != null && posterPath != null) {
                                movieList.add(new Movie(title, posterPath));
                            }
                        }
                        // 데이터 변경 후 어댑터에 알리기
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private class RatingHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        private RatingHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Movie movie) {
            Log.d("Movie", "Poster URL: " + movie.getPosterPath());

            // Glide를 사용해 URL에서 이미지를 가져오기
            Glide.with(binding.imageViewPoster.getContext())
                    .load(movie.getPosterPath())
                    .into(binding.imageViewPoster);

            // 영화 제목과 사용자 평가 바인딩
            binding.textViewTitle.setText(movie.getTitle());

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RatedMovieActivity.class);
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("posterPath", movie.getPosterPath());
                v.getContext().startActivity(intent);
            });
        }
    }

    private class RatingAdapter extends RecyclerView.Adapter<RatingHolder> {
        private final List<Movie> movies;

        public RatingAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        @NonNull
        @Override
        public RatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMovieBinding binding = ItemMovieBinding.inflate(getLayoutInflater(), parent, false);
            return new RatingHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RatingHolder holder, int position) {
            holder.bind(movies.get(position));
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }
    }
}
