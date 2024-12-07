package com.example.cinelog;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private Button submitVoteButton;
    private TextView questionText;
    private String selectedOption; // 선택된 옵션
    private List<Vote> options = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView selected_title;
    private Vote selectedPosition;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String selectedOption);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_vote, container, false);

        recyclerView = view.findViewById(R.id.vote_options_recycler);
        submitVoteButton = view.findViewById(R.id.submit_vote_button);
        questionText = view.findViewById(R.id.vote_question);
        selected_title = view.findViewById(R.id.selected_title);

        // 질문 설정
        questionText.setText("국내 최고의 느와르 영화는?");

        // Firestore에서 옵션 로드
        loadVoteOptions();

        // 투표 버튼 클릭 리스너
        submitVoteButton.setOnClickListener(v -> submitVote());

        return view;
    }

    private void loadVoteOptions() {
        db.collection("votes").document("noir_vote")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        options.clear();
                        List<Map<String, Object>> optionData = (List<Map<String, Object>>) documentSnapshot.get("options");
                        for (Map<String, Object> option : optionData) {
                            options.add(new Vote(
                                    (String) option.get("title"),
                                    (String) option.get("image"),
                                    ((Long) option.get("votes")).intValue()
                            ));
                        }
                        setupRecyclerView();
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void setupRecyclerView() {
        VoteOptionAdapter adapter = new VoteOptionAdapter(options, selected -> selectedOption = selected);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(16));
    }

    private void submitVote() {
        if (selectedOption == null) {
            Toast.makeText(getContext(), "영화를 선택하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("votes").document("noir_vote")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> optionData = (List<Map<String, Object>>) documentSnapshot.get("options");
                        if (optionData != null) {
                            for (Map<String, Object> option : optionData) {
                                if (selectedOption.equals(option.get("title"))) {
                                    int currentVotes = ((Long) option.get("votes")).intValue(); // 현재 투표 수 가져오기
                                    option.put("votes", currentVotes + 1); // 투표 수 증가
                                    break;
                                }
                            }

                            // Firebase에 업데이트된 데이터를 다시 저장
                            db.collection("votes").document("noir_vote")
                                    .update("options", optionData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "투표 완료!", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "투표 실패! 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    public  class VoteOptionAdapter extends RecyclerView.Adapter<VoteOptionAdapter.VoteOptionViewHolder> {

        private final List<Vote> options;
        private final OnOptionSelectedListener listener;

        public VoteOptionAdapter(List<Vote> options, OnOptionSelectedListener listener) {
            this.options = options;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VoteOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote, parent, false);
            return new VoteOptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VoteOptionViewHolder holder, int position) {
            Vote option = options.get(position);

            holder.title.setText(option.getTitle());
            Glide.with(holder.image.getContext()).load(option.getImage()).into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_title.setText("선택한 영화: " + option.getTitle());
                    selectedOption = option.getTitle();
                }
            });

        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        public  class VoteOptionViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView image;

            public VoteOptionViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.vote_movie_title);
                image = itemView.findViewById(R.id.vote_movie_poster);
            }
        }


    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public GridSpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.top = spacing;
            outRect.bottom = spacing;
        }
    }

}
