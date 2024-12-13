package com.example.cinelog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<String> imageUrls;
    private RecyclerView recyclerView;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ConstraintLayout korCommunityLayout = view.findViewById(R.id.kor_com_constraintLayout);

        korCommunityLayout.setOnClickListener(new View.OnClickListener() {  // 국내 영화 게시판 클릭리스너
            @Override
            public void onClick(View view) {
                Log.d("HomeFragment", "ConstraintLayout 클릭됨");
                Intent intent = new Intent(requireContext(), kor_Community.class);
                startActivity(intent);
            }
        });

        ConstraintLayout forCommunityLayout = view.findViewById(R.id.for_constraintLayout);





        ImageView searchbutton = view.findViewById(R.id.search_button);

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), Home_SearchActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.mylog_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager
                (requireContext(),LinearLayoutManager.HORIZONTAL,false));

        imageUrls = new ArrayList<>();

        RecyclerView.Adapter<MovieImageViewHolder> adapter = new RecyclerView.Adapter<MovieImageViewHolder>() {
            @NonNull
            @Override
            public MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰 홀더를 생성 함
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_mylog_item,parent,false);

                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.width = parent.getWidth() / 4; // 화면 폭의 1/4로 설정
                itemView.setLayoutParams(layoutParams);

                return new MovieImageViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull MovieImageViewHolder holder, int position) {
                String imageUrl = imageUrls.get(position);




                    Glide.with(holder.imageView.getContext())
                            .load(imageUrl)
                            .into(holder.imageView);



            }

            @Override
            public int getItemCount() {
                return imageUrls.size();
            }
        };

        recyclerView.setAdapter(adapter);

        loadMovieImages(adapter);



        // 게사판에 게시물 미리보기 구현
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView title1_text = view.findViewById(R.id.community_log1);
        TextView title2_text = view.findViewById(R.id.community_log2);
        TextView title3_text = view.findViewById(R.id.community_log3);


        db.collection("posts").whereEqualTo("isSpoiler",false)
                .limit(3).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        // 첫 번째 게시물
                        DocumentSnapshot doc1 = queryDocumentSnapshots.getDocuments().get(0);
                        String title1 = doc1.getString("title");
                        title1_text.setText(title1 != null ? title1 : "제목 없음");

                        // 두 번째 게시물
                        if (queryDocumentSnapshots.size() > 1) {
                            DocumentSnapshot doc2 = queryDocumentSnapshots.getDocuments().get(1);
                            String title2 = doc2.getString("title");
                            title2_text.setText(title2);
                        }

                        // 세 번째 게시물
                        if (queryDocumentSnapshots.size() > 2) {
                            DocumentSnapshot doc3 = queryDocumentSnapshots.getDocuments().get(2);
                            String title3 = doc3.getString("title");
                            title3_text.setText(title3);
                        }
                    } else {
                        Log.e("Firestore", "No posts found");
                    }

                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching posts", e));

        RecyclerView recyclerView = view.findViewById(R.id.app_recyclerview);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<AppItem> appItems = new ArrayList<>();
        appItems.add(new AppItem("https://images.ctfassets.net/y2ske730sjqp/5QQ9SVIdc1tmkqrtFnG9U1/de758bba0f65dcc1c6bc1f31f161003d/BrandAssets_Logos_02-NSymbol.jpg?w=940", "com.netflix.mediaclient")); //Netflix
        appItems.add(new AppItem("https://yt3.googleusercontent.com/pCcTLy8Zj7gIuo3yfYkB6cT2f0jz2beWJC5E4-B4ju9VBLdfrVDi6yc0B0313N8EVLY1UBaxxA=s900-c-k-c0x00ffffff-no-rj","com.frograms.wplay"));
        appItems.add(new AppItem("https://yt3.googleusercontent.com/AwhuiDrnSIKzxLyp48jc5dyxj7YVdpwrj42s11o0slC0_sAOVQDDASFU9q3fDwan8UKEJP0Wew=s900-c-k-c0x00ffffff-no-rj","kr.co.captv.pooqV2"));
        appItems.add(new AppItem("https://yt3.googleusercontent.com/y8xDKfp1aHjwej33BIhVNcaJnHgKke2jB6bHkrrpckJO7SxyFvvDpPRbIwO0kxGcZOnAOHkCfQ=s900-c-k-c0x00ffffff-no-rj","com.disney.disneyplus"));
        appItems.add(new AppItem("https://play-lh.googleusercontent.com/IPu4haF4Jl9sMQ8TUEYJ4zUtN9pHJuxLOZzGHQcRPeT5ud07Y4sgUlB6ITaaxtbsPVA","com.coupang.mobile.play"));
        appItems.add(new AppItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQB9WVNPJ67lWIeBlUJ5GNfo51fcRyYEHnuGQ&s", "com.amazon.avod.thirdpartyclient"));//Amazon

        // 어댑터 설정
        CircleImageAdapter ottadapter = new CircleImageAdapter(getContext(),appItems);
        recyclerView.setAdapter(ottadapter);


    }


    private void loadMovieImages(RecyclerView.Adapter<?> adapter){  // 리사이클러뷰에 이미지 띄우는 메서드
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("ratings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots  -> {
                    imageUrls.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String posterUrl = document.getString("posterPath"); // 'posterPath' 필드 가져오기
                        if (posterUrl != null) {
                            imageUrls.add(posterUrl); // 이미지 URL 리스트에 추가
                        }
                        else {
                            System.out.println("Document missing posterPath: " + document.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });

    }


    private static class MovieImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MovieImageViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.mylog_poster_image);
        }
    }

    public class CircleImageAdapter extends RecyclerView.Adapter<CircleImageAdapter.CircleViewHolder> {

        private List<AppItem> appItems;
        private Context context;

        public CircleImageAdapter(Context context, List<AppItem> appItems) {
            this.context = context;
            this.appItems = appItems;
        }

        @NonNull
        @Override
        public CircleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ott_circle_image, parent, false);
            return new CircleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CircleViewHolder holder, int position) {
            AppItem appItem = appItems.get(position);

            // Glide를 사용하여 이미지 로드
            Glide.with(context)
                    .load(appItem.getImageUrl())
                    .circleCrop()
                    .into(holder.imageView);

            // 클릭 이벤트 추가
            holder.imageView.setOnClickListener(v -> {
                String packageName = appItem.getPackageName();
                openApp(packageName);
            });
        }

        @Override
        public int getItemCount() {
            return appItems.size();
        }

        public class CircleViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public CircleViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageViewCircle);
            }
        }

        private void openApp(String packageName) {
            try {
                // 앱 실행을 위한 인텐트 생성
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    context.startActivity(intent); // 앱 실행
                } else {
                    // 앱이 설치되어 있지 않을 경우 Play Store로 이동
                    redirectToPlayStore(packageName);
                }
            } catch (Exception e) {
                Log.e("CircleImageAdapter", "Error launching app: " + packageName, e);
                redirectToPlayStore(packageName);
            }
        }

        // Play Store로 리디렉션
        private void redirectToPlayStore(String packageName) {
            try {
                // Play Store로 이동
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                // Play Store가 없을 경우 웹 브라우저로 이동
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        }

    }

}

























