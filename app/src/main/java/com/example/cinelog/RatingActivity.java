package com.example.cinelog;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cinelog.databinding.ActivityRatingBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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

        db.collection("users")
                .document(mAuth.getUid())
                .collection("ratings")
                .whereEqualTo("title", title) // "title" 필드가 특정 값과 같은 문서 검색
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fetchedTitle = document.getString("title");

                            if (fetchedTitle != null) {
                                Log.d("Firestore", "Movie found: " + fetchedTitle);
                                binding.title.setText(fetchedTitle);
                                Glide.with(binding.poster.getContext())
                                        .load(document.getString("posterPath"))
                                        .into(binding.poster);
                                binding.comment.setText(document.getString("comment"));
                                binding.date.setText(document.getString("date"));
                                binding.place.setText(document.getString("place"));
                                binding.friend.setText(document.getString("friend"));
                                binding.memo.setText(document.getString("memo"));
                                binding.ratingbar.setRating(document.getLong("rating"));
                            }

                        }

                        if (task.getResult().isEmpty()) {
                            Log.d("Firestore", "No movie found with title: " + title);
                        }
                    } else {
                        Log.e("Firestore", "Error fetching movie by title: ", task.getException());
                    }
                });

        RatingBar ratingBar = binding.ratingbar;
        EditText commentEditText = binding.comment;
        TextView dateEditText = binding.date;
        EditText placeEditText = binding.place;
        EditText friendEditText = binding.friend;
        EditText memoEditText = binding.memo;
        Button saveButton = binding.saveButton;
        dateEditText.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.show(getSupportFragmentManager(), "datePicker");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

                dateEditText.setText(formattedDate);
            });
        });


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

            Intent intentToRatingList = new Intent(this, NavigationBar.class)
                    .putExtra("fragment", "CalendarFragment");
            startActivity(intentToRatingList);
        });

        binding.backButton.setOnClickListener(view -> {
            finish();
        });
    }
}