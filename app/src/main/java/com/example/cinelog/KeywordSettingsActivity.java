package com.example.cinelog;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinelog.databinding.ActivityKeywordSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class KeywordSettingsActivity extends AppCompatActivity {

    private static final int REQUEST_POST_NOTIFICATIONS = 1;

    private RecyclerView recyclerView;
    private KeywordAdapter keywordAdapter;
    private List<String> keywordList;
    private List<String> filteredKeywordList;
    private ImageView btnBack;
    private EditText etNewKeyword;
    private Button btnAddKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityKeywordSettingsBinding binding = ActivityKeywordSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.rvKeywordList;
        btnBack = binding.btnBack;
        etNewKeyword = binding.etNewKeyword;
        btnAddKeyword = binding.btnAddKeyword;

        keywordList = new ArrayList<>();
        filteredKeywordList = new ArrayList<>();

        keywordAdapter = new KeywordAdapter(filteredKeywordList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(keywordAdapter);

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }

        btnBack.setOnClickListener(v -> onBackPressed());

        btnAddKeyword.setOnClickListener(v -> {
            String newKeyword = etNewKeyword.getText().toString().trim();
            if (!newKeyword.isEmpty()) {
                addKeywordToFirestore(newKeyword);
                etNewKeyword.setText("");
            }
        });

        binding.etSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterKeywords(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        loadKeywordsFromFirestore();
    }

    private void addKeywordToFirestore(String keyword) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .collection("keywords").document(keyword)
                .set(new Keyword(keyword, 0))
                .addOnSuccessListener(aVoid -> {
                    keywordList.add(keyword);
                    filterKeywords("");
                })
                .addOnFailureListener(e -> Log.e("KeywordSettings", "Error adding keyword", e));
    }

    private void loadKeywordsFromFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .collection("keywords")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    keywordList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String keyword = doc.getString("keyword");
                        if (keyword != null) keywordList.add(keyword);
                    }
                    filterKeywords("");
                })
                .addOnFailureListener(e -> Log.e("KeywordSettings", "Error loading keywords", e));
    }

    private void filterKeywords(String query) {
        filteredKeywordList.clear();

        if (query.isEmpty()) {
            filteredKeywordList.addAll(keywordList);
        } else {
            String normalizedQuery = query.trim().toLowerCase();

            for (String keyword : keywordList) {
                if (keyword.toLowerCase().contains(normalizedQuery)) {
                    filteredKeywordList.add(keyword);
                }
            }
        }

        keywordAdapter.notifyDataSetChanged();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Keyword Alerts";
            String description = "Notifications for matching keywords in posts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("KEYWORD_ALERT", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Notification", "Permission not granted to send notifications.");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "KEYWORD_ALERT")
                .setSmallIcon(R.drawable.ic_help)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void incrementKeywordCount(String keyword) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .collection("keywords").document(keyword)
                .update("count", FieldValue.increment(1))
                .addOnFailureListener(e -> Log.e("KeywordCount", "Error updating count", e));
    }

    public void checkForKeywordMatch(String title, String content) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .collection("keywords").get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String keyword = doc.getString("keyword");
                        if (keyword != null && (title.contains(keyword) || content.contains(keyword))) {
                            sendNotification("키워드 알림", "매칭된 키워드: " + keyword);
                            incrementKeywordCount(keyword);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("KeywordMatch", "Error checking keywords", e));
    }

    private void requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "POST_NOTIFICATIONS permission granted.");
            } else {
                Log.e("Permission", "POST_NOTIFICATIONS permission denied.");
            }
        }
    }

}




