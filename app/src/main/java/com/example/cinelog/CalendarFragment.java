package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.seperate_line));
        recyclerView.addItemDecoration(dividerItemDecoration);


        adapter = new MyAdapter(movieList);
        recyclerView.setAdapter(adapter);

        // 데이터 가져오기
        fetchMovies();

        ImageButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), Home_SearchActivity.class));
        });

        ImageButton listButton = view.findViewById(R.id.list_button);
        listButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), RatingListActivity.class));
        });

        TextView calendarText = view.findViewById(R.id.calendar_text);
        calendarText.setOnClickListener(view1 -> {
            getParentFragmentManager().beginTransaction()
                    .replace(getId(), new CustomCalendarFragment())
                    .commit();
        });
    }

    private void fetchMovies() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("ratings")
                .orderBy("date")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieList.clear();
                        String previousYearMonth = "";

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String posterPath = document.getString("posterPath");
                            Float rating = ((Double) document.get("rating")).floatValue();
                            String date = document.getString("date"); // e.g., "2024-08-12"

                            StringTokenizer split =new StringTokenizer(date,".");
                            String yearMonth = split.nextToken() + "년 " + split.nextToken() + "월";

                            // 새로운 년/월이면 추가
                            if (!yearMonth.equals(previousYearMonth)) {
                                movieList.add(new Movie(Movie.TYPE_YEAR_MONTH, yearMonth));
                                previousYearMonth = yearMonth;
                            }

                            // 영화 데이터 추가
                            if (title != null && posterPath != null) {
                                movieList.add(new Movie(Movie.TYPE_MOVIE, title, posterPath, rating, split.nextToken())); // parts[2] = day
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }



    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Movie> movieList;

        public MyAdapter(List<Movie> movieList) {
            this.movieList = movieList;
        }

        @Override
        public int getItemViewType(int position) {
            return movieList.get(position).getViewType();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == Movie.TYPE_YEAR_MONTH) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_year_month, parent, false);
                return new YearMonthViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_calendar, parent, false);
                return new MovieViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Movie movie = movieList.get(position);

            if (holder instanceof YearMonthViewHolder) {
                ((YearMonthViewHolder) holder).bind(movie);
            } else if (holder instanceof MovieViewHolder) {
                ((MovieViewHolder) holder).bind(movie);
            }
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }

        class YearMonthViewHolder extends RecyclerView.ViewHolder {
            private final TextView yearMonthTextView;

            public YearMonthViewHolder(View itemView) {
                super(itemView);
                yearMonthTextView = itemView.findViewById(R.id.year_month_text);
            }

            public void bind(Movie movie) {
                yearMonthTextView.setText(movie.getDate());
            }
        }

        // ViewHolder - 영화 기록
        class MovieViewHolder extends RecyclerView.ViewHolder {
            private final TextView dateTextView;
            private final TextView titleTextView;
            private final RatingBar ratingBar;
            private final ImageView posterImageView;

            public MovieViewHolder(View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.calendar_date);
                titleTextView = itemView.findViewById(R.id.calendar_title);
                ratingBar = itemView.findViewById(R.id.calendar_rating);
                posterImageView = itemView.findViewById(R.id.calendar_poster);

                itemView.findViewById(R.id.calendar_layout).setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    Movie movie = movieList.get(position);
                    Intent intent = new Intent(view.getContext(), RatedMovieActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("posterPath", movie.getPosterPath());
                    view.getContext().startActivity(intent);
                });
            }

            public void bind(Movie movie) {
                dateTextView.setText(movie.getDate());
                titleTextView.setText(movie.getTitle());
                ratingBar.setRating(movie.getRating());

                Glide.with(posterImageView.getContext())
                        .load(movie.getPosterPath())
                        .into(posterImageView);
            }
        }
    }


}