package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Movie> movieList = new ArrayList<>();
    private MyAdapter adapter;

    public CalendarFragment() {

    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.calendar_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // 어댑터 초기화
        adapter = new MyAdapter(movieList);
        recyclerView.setAdapter(adapter);

        // 데이터 가져오기
        fetchMovies();
    }

    private void fetchMovies() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("ratings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 데이터가 성공적으로 불러와졌을 때 movieList를 업데이트
                        movieList.clear(); // 이전 데이터를 삭제
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String posterPath = document.getString("posterPath");
                            Float rating = ((Double) document.get("rating")).floatValue();
                            String date = document.getString("date");

                            if (title != null && posterPath != null) {
                                movieList.add(new Movie(title, posterPath, rating, date));
                            }
                        }
                        // 데이터 변경 후 어댑터에 알리기
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<Movie> movieList;

        // ViewHolder 클래스 정의
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView dateTextView;
            private final TextView titleTextView;
            private final RatingBar ratingBar;
            private final ImageView posterImageView;

            public ViewHolder(View view) {
                super(view);
                dateTextView = view.findViewById(R.id.calendar_date);
                titleTextView = view.findViewById(R.id.calendar_title);
                ratingBar = view.findViewById(R.id.calendar_rating);
                posterImageView = view.findViewById(R.id.calendar_poster);


                view.findViewById(R.id.calendar_layout).setOnClickListener(view1 -> {
                    int position = getBindingAdapterPosition();
                    Movie movie = movieList.get(position);
                    Intent intent = new Intent(view1.getContext(), RatedMovieActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("posterPath", movie.getPosterPath());
                    view1.getContext().startActivity(intent);
                });
            }
        }

        // 생성자: 데이터 리스트를 받아 저장
        public MyAdapter(List<Movie> movieList) {
            this.movieList = movieList;
        }

        // ViewHolder 생성
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_calendar, parent, false);
            return new ViewHolder(view);
        }

        // ViewHolder에 데이터 바인딩
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Movie movie = movieList.get(position);
            holder.dateTextView.setText(movie.getDate());
            holder.titleTextView.setText(movie.getTitle());
            holder.ratingBar.setRating(movie.getRating());

            Glide.with(holder.posterImageView.getContext())
                    .load(movie.getPosterPath()) // 포스터 URL
                    .into(holder.posterImageView);


        }

        // 데이터셋 크기 반환
        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }

}