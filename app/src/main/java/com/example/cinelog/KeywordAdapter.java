package com.example.cinelog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private List<String> keywordList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public KeywordAdapter(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_item, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        String keyword = keywordList.get(position);
        holder.tvKeyword.setText(keyword);

        holder.ivDelete.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid(); // 현재 유저 ID 가져오기
            db.collection("users")
                    .document(userId)
                    .collection("keywords")
                    .document(keyword) // 키워드 이름을 문서 ID로 사용
                    .delete() // Firestore에서 삭제
                    .addOnSuccessListener(aVoid -> {
                        // Firestore에서 성공적으로 삭제된 경우, 리스트에서도 삭제
                        keywordList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, keywordList.size());
                    })
                    .addOnFailureListener(e -> {
                        // 삭제 실패 시 처리
                        Log.e("KeywordAdapter", "Failed to delete keyword: " + keyword, e);
                    });
        });

//        holder.ivEdit.setOnClickListener(v -> {
//
//        });
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;
        ImageView ivDelete;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tv_keyword);
            //ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}





