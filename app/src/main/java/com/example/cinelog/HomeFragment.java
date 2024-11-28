package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> imageUrls;
    private RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
}

























