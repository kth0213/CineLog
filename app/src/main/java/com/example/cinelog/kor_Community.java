package com.example.cinelog;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class kor_Community extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Switch spoilerSwitch;

    private FirebaseFirestore db;
    private List<Post> postList;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kor_community);

        Log.d("kor_com", "kor_com_created");

        db = FirebaseFirestore.getInstance();

        SwitchCompat switchCompat = findViewById(R.id.spoiler_switch);
        RecyclerView recyclerView = findViewById(R.id.kor_community_recyclerview);

        postList = new ArrayList<>();

        ImageView back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        Button vote_button = findViewById(R.id.vote_but);
//        vote_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                VoteBottomSheetDialogFragment voteSheet = new VoteBottomSheetDialogFragment();
//                voteSheet.show(getSupportFragmentManager(), "VoteSheet");
//
//            }
//        });



        Button write_but = findViewById(R.id.write);

        write_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(kor_Community.this,Writing_Activity.class));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList);
        recyclerView.setAdapter(adapter);

        if (switchCompat == null) {
            Log.e("kor_Community", "spoiler_switch is null. Check your XML layout.");
        } else {

            // 스포일러 스위치 상태에 따라 초기 데이터 로드
            boolean initialFilter = switchCompat.isChecked();
            loadPosts(initialFilter);

            // 스위치 상태 변경 리스너
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> loadPosts(isChecked));
        }


    }
    private void loadPosts(boolean filterSpoiler) {
        Log.d("kor_Community", "Loading posts. Filter for spoiler: " + filterSpoiler);

        db.collection("posts")
                .whereEqualTo("isSpoiler", filterSpoiler) // 스포일러 필터 조건
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    postList.clear();
                    if (!querySnapshot.isEmpty()) {
                        Log.d("kor_Community", "Query returned " + querySnapshot.size() + " documents.");
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            if (doc.exists()) {
                                Post post = doc.toObject(Post.class);
                                if (post != null) {
                                    Log.d("kor_Community", "Post loaded: " + post.getTitle());
                                    postList.add(post);
                                } else {
                                    Log.e("kor_Community", "Failed to convert document to Post: " + doc.getId());
                                }
                            }
                        }
                    } else {
                        Log.d("kor_Community", "No posts found for filter: " + filterSpoiler);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("kor_Community", "Error loading posts", e);
                    Toast.makeText(this, "게시물을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });

    }

    public class  PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
        private List<Post> postList;

        public PostAdapter(List<Post> postList){ this.postList = postList;}

        @NonNull
        @Override
        public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kor_com, parent, false);
            return new PostViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
            Post post = postList.get(position);

            holder.title.setText(post.getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), kor_Community_log.class);

                    Date date = post.getTimestamp().toDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yy.M.d HH:mm", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);

                    intent.putExtra("title",post.getTitle());
                    intent.putExtra("content",post.getContent());
                    intent.putExtra("author",post.getAuthor());
                    intent.putExtra("timestamp",formattedDate);
                    intent.putExtra("id",post.getId());

                    view.getContext().startActivity(intent);

                }
            });


            // 시간 설정
            // 시간 설정 (타임스탬프 처리)
            Timestamp timestamp = post.getTimestamp();
            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(date);
                holder.timestamp.setText("작성 시간: " + formattedDate);
            }

        }

        @Override
        public int getItemCount() {
            return postList.size();
        }

        public  class PostViewHolder extends RecyclerView.ViewHolder {
            TextView title, author, timestamp;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.com_title);
                author = itemView.findViewById(R.id.com_author);
                timestamp = itemView.findViewById(R.id.com_time);


            }
        }
    }
}




