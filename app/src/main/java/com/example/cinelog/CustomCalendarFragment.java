package com.example.cinelog;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinelog.Day;
import com.example.cinelog.databinding.FragmentCustomCalendarBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class CustomCalendarFragment extends Fragment {
    private FragmentCustomCalendarBinding binding;
    private Calendar calendar;
    private SimpleDateFormat monthYearFormat;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Map<String, List<Movie>> movieMap = new HashMap<>(); // 날짜별 영화 데이터를 저장

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomCalendarBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        calendar = Calendar.getInstance(); // 현재 월/년도 설정
        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        // 초기 월/년도 표시
        updateMonthYearDisplay();

        fetchMoviesForMonth();

        // 화살표 버튼 클릭 이벤트 설정
        binding.btnPreviousMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1); // 이전 월로 이동
            updateMonthYearDisplay();
            fetchMoviesForMonth(); // 해당 월의 데이터를 다시 가져옴
        });

        binding.btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1); // 다음 월로 이동
            updateMonthYearDisplay();
            fetchMoviesForMonth(); // 해당 월의 데이터를 다시 가져옴
        });


        // ViewPager2 설정
        binding.viewPager.setAdapter(new CalendarPagerAdapter(this));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton addButton = view.findViewById(R.id.add_button_c);
        addButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), Home_SearchActivity.class));
        });

        ImageButton listButton = view.findViewById(R.id.list_button_c);
        listButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), RatingListActivity.class));
        });

        TextView calendarText = view.findViewById(R.id.calendar_text_c);
        calendarText.setOnClickListener(view1 -> {
            getParentFragmentManager().beginTransaction()
                    .replace(getId(), new CalendarFragment())
                    .commit();
        });
    }

    private void updateMonthYearDisplay() {
        String monthYear = monthYearFormat.format(calendar.getTime());
        binding.textMonthYear.setText(monthYear);
    }

    private void fetchMoviesForMonth() {
        String uid = FirebaseAuth.getInstance().getUid();

        // 현재 선택된 년도와 월
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        // 시작 날짜와 끝 날짜 생성
        String startDate = String.format(Locale.getDefault(), "%04d.%02d.01", year, month);
        String endDate = String.format(Locale.getDefault(), "%04d.%02d.31", year, month);

        // Firestore 쿼리
        db.collection("users").document(uid).collection("ratings")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieMap.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String posterPath = document.getString("posterPath");
                            String date = document.getString("date");

                            if (title != null && posterPath != null && date != null) {
                                Movie movie = new Movie(title, posterPath);
                               // Log.d("PosterPath", "Date: " + date + ", Poster URL: " + movie.getPosterPath());

                                // 날짜를 키로 사용
                                if (!movieMap.containsKey(date)) {
                                    movieMap.put(date, new ArrayList<>());
                                }
                                movieMap.get(date).add(movie);
                            }
                        }


                        // 데이터 로드 후 RecyclerView 갱신
                        binding.viewPager.getAdapter().notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error fetching movies: ", task.getException());
                    }
                });
    }





    private class CalendarPagerAdapter extends RecyclerView.Adapter<CalendarPagerAdapter.MonthViewHolder> {
        private final Fragment fragment;

        public CalendarPagerAdapter(Fragment fragment) {
            this.fragment = fragment;
        }

        @NonNull
        @Override
        public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView recyclerView = new RecyclerView(parent.getContext());
            recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, // 가로를 match_parent로 설정
                    RecyclerView.LayoutParams.MATCH_PARENT  // 세로를 match_parent로 설정
            ));
            recyclerView.setLayoutManager(new GridLayoutManager(parent.getContext(), 7)); // 7일 기준
            return new MonthViewHolder(recyclerView);
        }


        @Override
        public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
            int year = calendar.get(Calendar.YEAR);
            int month = (calendar.get(Calendar.MONTH) + position) % 12;

            // 날짜 리스트 생성
            List<Day> days = generateDaysForMonth(year, month);
            CalendarAdapter adapter = new CalendarAdapter(days);
            holder.recyclerView.setAdapter(adapter);
        }


        @Override
        public int getItemCount() {
            return 12; // 12달
        }

        private List<Day> generateDaysForMonth(int year, int month) {
            List<Day> days = new ArrayList<>();

            // Calendar 객체를 사용해 월의 최대 일 수 계산
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1); // 해당 월로 설정
            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            // 날짜 리스트 생성
            for (int i = 1; i <= maxDays; i++) {
                String dateKey = String.format(Locale.getDefault(), "%04d.%02d.%02d", year, month+1, i);
                Log.d("PosterPath", "dateKey: " + dateKey);
                // 날짜별로 포스터 URL 매핑
                if (movieMap.containsKey(dateKey)) {
                    List<Movie> movies = movieMap.get(dateKey);
                    Log.d("PosterPath", "dateKey: " + dateKey + ", movies: " + movies.size() + "개");
                    if (movies != null && !movies.isEmpty()) {
                        String posterUrl = movies.get(0).getPosterPath(); // 첫 번째 영화의 포스터만 사용

                        Log.d("PosterPath", "Date: " + movies.get(0).getDate() +"i"+i+ ", Poster URL: " + movies.get(0).getPosterPath());
                        days.add(new Day(i, posterUrl));
                    } else {
                        days.add(new Day(i, null)); // 포스터가 없는 경우
                    }
                } else {
                    days.add(new Day(i, null)); // 데이터가 없는 경우
                }
            }

            return days;
        }



        class MonthViewHolder extends RecyclerView.ViewHolder {
            RecyclerView recyclerView;

            public MonthViewHolder(@NonNull View itemView) {
                super(itemView);
                recyclerView = (RecyclerView) itemView;
            }
        }
    }


    public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {
        private final List<Day> days;

        public CalendarAdapter(List<Day> days) {
            this.days = days;
        }

        @NonNull
        @Override
        public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            com.example.cinelog.databinding.ItemDayBinding binding = com.example.cinelog.databinding.ItemDayBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new DayViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
            Day day = days.get(position);
            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return days.size();
        }
        class DayViewHolder extends RecyclerView.ViewHolder {
            private final com.example.cinelog.databinding.ItemDayBinding binding;

            public DayViewHolder(com.example.cinelog.databinding.ItemDayBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(Day day) {
                binding.textViewDay.setText(String.valueOf(day.getDate()));

                // 영화 포스터 표시
                if (day.getPosterUrl() != null) {
                    // Glide로 이미지를 로드
                    Glide.with(requireContext())  // 명시적으로 Context 사용
                            .load(day.getPosterUrl())
                            .placeholder(android.R.color.darker_gray)  // 로딩 중에는 placeholder 표시
                            .error(android.R.color.transparent)  // 오류 발생 시 빈 이미지 표시
                            .into(binding.imageViewPoster);
                } else {
                    binding.imageViewPoster.setImageResource(android.R.color.transparent); // 포스터가 없으면 빈 이미지
                }
            }

        }
    }

}
