package com.example.cinelog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cinelog.databinding.ActivityNavigationBarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NavigationBar extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNavigationBarBinding binding = ActivityNavigationBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission();
        }
        createNotificationChannel();
        listenForNewPosts();

        BottomNavigationView bottomNavigationView = binding.navigation;

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.action_home){
                    transferTo(HomeFragment.newInstance("param1","param2"));
                    return true;
                }
                if(itemId == R.id.action_calendar){
                    transferTo(CalendarFragment.newInstance("param1","param2"));
                    return true;
                }
                if(itemId == R.id.action_profile){
                    transferTo(new ProfileFragment());
                    return true;
                }
                return false;
            }
        });

        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId==R.id.action_calendar){
                    if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof CalendarFragment){
                        transferTo(new CustomCalendarFragment());
                    }
                    else{
                        transferTo(CalendarFragment.newInstance("param1","param2"));
                    }
                }
            }
        });

        if(getIntent().hasExtra("fragment")){
            if(getIntent().getStringExtra("fragment").equals("CalendarFragment")) {
                transferTo(CalendarFragment.newInstance("param1","param2"));
                bottomNavigationView.setSelectedItemId(R.id.action_calendar);
            } else if (getIntent().getStringExtra("fragment").equals("ProfileFragment")) {
                transferTo(new ProfileFragment());
                bottomNavigationView.setSelectedItemId(R.id.action_profile);
            }
        }
        else {
            transferTo(HomeFragment.newInstance("param1","param2"));
        }
    }
    private void transferTo(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    private void listenForNewPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("posts")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("FirestoreListener", "Error listening for new posts", error);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                String title = dc.getDocument().getString("title");
                                String content = dc.getDocument().getString("content");
                                String postID = dc.getDocument().getId();

                                checkForKeywordMatch(userId, title, content,postID); // 키워드 매칭 확인
                            }
                        }
                    }
                });
    }
    private void checkForKeywordMatch(String userId, String title, String content, String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).collection("notifications")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("Notification", "Already notified for post: " + postId);
                    } else {
                        matchKeywordsAndNotify(userId, title, content, postId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Notification", "Error checking notification status", e));
    }
    private void matchKeywordsAndNotify(String userId, String title, String content, String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("keywords")
                .get()
                .addOnSuccessListener(keywordsSnapshot -> {
                    for (DocumentSnapshot keywordDoc : keywordsSnapshot.getDocuments()) {
                        String keyword = keywordDoc.getString("keyword");

                        if (keyword != null && (title.contains(keyword) || content.contains(keyword))) {
                            sendLocalNotification(keyword, title,postId);

                            // 알림 상태 저장
                            db.collection("users").document(userId).collection("notifications")
                                    .document(postId)
                                    .set(new HashMap<String, Object>() {{
                                        put("notified", true);
                                    }});
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("KeywordMatch", "Error checking keywords", e));
    }

    private void sendLocalNotification(String keyword, String title,String postId) {
        Intent intent = new Intent(this, kor_Community_log.class);
        intent.putExtra("id", postId); // 전달할 데이터 추가
        intent.putExtra("POST_TITLE", title);

        // 알림 클릭 시 실행될 작업 설정
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                postId.hashCode(), // 고유 ID를 사용
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "KEYWORD_ALERT")
                .setSmallIcon(R.drawable.baseline_bookmark_border_24)
                .setContentTitle("키워드 알림")
                .setContentText("키워드 '" + keyword + "'가 포함된 글이 작성되었습니다: " + title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // 알림에 PendingIntent 추가
                .setAutoCancel(true); // 알림 클릭 시 자동으로 닫힘

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } else {
            Log.e("Notification", "POST_NOTIFICATIONS permission not granted");
            requestNotificationPermission();
        }
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
    private static final int REQUEST_POST_NOTIFICATIONS = 101;

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
        }
    }




}