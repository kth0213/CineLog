package com.example.cinelog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.KeywordViewHolder> {

    private List<String> keywordList;

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
            keywordList.remove(position);
            notifyItemRemoved(position);
        });

        holder.ivEdit.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;
        ImageView ivEdit, ivDelete;

        public KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tv_keyword);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}





