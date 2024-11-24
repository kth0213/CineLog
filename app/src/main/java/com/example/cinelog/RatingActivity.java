package com.example.cinelog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cinelog.databinding.ActivityRatingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRatingBinding binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String posterUrl = intent.getStringExtra("posterUrl");
        Glide.with(binding.poster.getContext())
                .load(posterUrl)
                .into(binding.poster);
        binding.title.setText(title);

        RatingBar ratingBar = binding.ratingbar;
        EditText commentEditText = binding.comment;
        EditText dateEditText = binding.date;
        EditText placeEditText = binding.place;
        EditText friendEditText = binding.friend;
        EditText memoEditText = binding.memo;
        Button saveButton = binding.saveButton;


        saveButton.setOnClickListener(view -> {
            String comment = commentEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String place = placeEditText.getText().toString();
            String friend = friendEditText.getText().toString();
            String memo = memoEditText.getText().toString();
            Log.d("Button", "Save button clicked");


            Map<String, Object> allData = new HashMap<>();
            allData.put("title",title);
            allData.put("posterPath",posterUrl);
            allData.put("comment", comment);
            allData.put("date", date);
            allData.put("place", place);
            allData.put("friend", friend);
            allData.put("memo", memo);
            allData.put("rating", ratingBar.getRating());

            db.collection("users/"+mAuth.getUid()+"/ratings").document(binding.title.getText().toString())
                    .set(allData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Data saved successfully"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error saving data", e));

            Intent intentToRatingList = new Intent(this, RatingListActivity.class);
            startActivity(intentToRatingList);
        });
    }
}