package com.example.cinelog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinelog.databinding.ActivityKeywordSettingsBinding;

import java.util.ArrayList;
import java.util.List;

public class KeywordSettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KeywordAdapter keywordAdapter;
    private List<String> keywordList;
    private List<String> filteredKeywordList;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityKeywordSettingsBinding binding = ActivityKeywordSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        recyclerView = binding.rvKeywordList;
        btnBack = binding.btnBack;


        keywordList = new ArrayList<>();
        filteredKeywordList = new ArrayList<>();
        populateDummyKeywords();

        keywordAdapter = new KeywordAdapter(filteredKeywordList);  // 필터된 키워드 리스트 사용
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(keywordAdapter);


        btnBack.setOnClickListener(v -> finish());


        binding.etSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                filterKeywords(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void populateDummyKeywords() {
        keywordList.add("봉준호 감독");
        keywordList.add("베테랑 2");
        keywordList.add("마틴 스콜세지");
        keywordList.add("엠마 왓슨");

        filteredKeywordList.addAll(keywordList);
    }


    private void filterKeywords(String query) {
        filteredKeywordList.clear();  //

        if (query.isEmpty()) {

            filteredKeywordList.addAll(keywordList);
        } else {

            String normalizedQuery = query.trim().toLowerCase();

            // 한국어 포함 필터링
            for (String keyword : keywordList) {
                if (keyword.toLowerCase().contains(normalizedQuery)) {
                    filteredKeywordList.add(keyword);
                }
            }
        }

        // Adapter에게 데이터가 변경되었음을 알리기
        keywordAdapter.notifyDataSetChanged();
    }

}

