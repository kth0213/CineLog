package com.example.cinelog;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.cinelog.databinding.ActivityHomeSearchBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



import java.util.ArrayList;
import java.util.List;


public class Home_SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeSearchBinding binding = ActivityHomeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SearchView searchView = binding.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {  // 앤터를 쳤을때 searchView에서 할일
                Log.d("YYS","onQueryTextSubmit");
                searchMovie(s);  // 같은 제목의 영화 찾기
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("YYS","onQueryTextChange");

                return false;
            }
        });

    }
    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

        private List<Search_Movie> movies;
        private Context context;

        public MovieAdapter(Context context, List<Search_Movie> movies){
            this.context = context;
            this.movies = movies;
        }

        @NonNull
        @Override
        public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // 뷰 홀더 생성

            Log.d("YYS","onCreateViewHolder");

            View view = LayoutInflater.from(context).inflate(R.layout.home_item_recycler,parent,false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {

            Search_Movie movie = movies.get(position); // 지금 표시할 영화 데이터 받기

            holder.titleTextView.setText(movie.getTitle());  // 위에서 정한 영화의 제목을 띄우기

            Glide.with(context)   // 그릴드 이용해서 영화 포스터 이미지 띄우기
                    .load(movie.getPosterUrl())
                    .into(holder.posterImageView);
        }

        @Override
        public int getItemCount() {
            return movies.size(); // 리스트 사이즈 만큼 리사이클러 뷰 띄우도록 설정
        }


        public class MovieViewHolder extends RecyclerView.ViewHolder{  // 뷰 홀더
            TextView titleTextView;
            ImageView posterImageView;
            TextView DirectorView;

            public MovieViewHolder(@NonNull View itemView) {

                super(itemView);

                titleTextView = itemView.findViewById(R.id.moive_title);  // 아이템 xml에서 영화 제목이랑 포스터 이미지 연결
                posterImageView = itemView.findViewById(R.id.moive_poster);
            }
        }
    }

    private void searchMovie(String query){

        Log.d("YYS","searchMovie");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies")
                .whereEqualTo("title",query)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Search_Movie> movieList = new ArrayList<>();

                    for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments() ){
                        String title = document.getString("title");
                        String posterUrl = document.getString("poster_url");
                        String director = document.getString("director");

                        movieList.add(new Search_Movie(title,posterUrl,director));
                    }
                    if(movieList.isEmpty()){
                        showNoResultsMessage();
                    }
                    else {
                        updateRecylerView(movieList);
                    }

                }).addOnFailureListener(e -> {
                    Toast.makeText(this,"검색 중 오류 발생",Toast.LENGTH_SHORT).show();
                });
    }

    private void updateRecylerView(List<Search_Movie> moives){

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        MovieAdapter moiveAdapter = new MovieAdapter(this,moives);

        recyclerView.setLayoutManager((new LinearLayoutManager(this)));
        recyclerView.setAdapter(moiveAdapter);
    }

    private void showNoResultsMessage(){
        Toast.makeText(this,"검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
    }

}

