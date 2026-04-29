package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNoti;
    private NotificationAdapter adapter;
    private List<Notification> allList = new ArrayList<>();
    private TextView tabAll, tabPromo, tabSystem;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // KẾT NỐI FIREBASE THEO USER ID
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("notifications").child(userId);
        }

        anhXa();
        fetchData();

        // ĐÂY LÀ DÒNG QUAN TRỌNG NHẤT BỊ THIẾU:
        setupBottomNavigation();

        // Xóa thông báo cũ (hơn 7 ngày)
        findViewById(R.id.btnClearOld).setOnClickListener(v -> clearOldNotifications());

        // Đánh dấu tất cả đã đọc
        findViewById(R.id.btnMarkAllRead).setOnClickListener(v -> markAllAsRead());
    }

    private void anhXa() {
        rvNoti = findViewById(R.id.rvNotifications);
        tabAll = findViewById(R.id.tabAll);
        tabPromo = findViewById(R.id.tabPromo);
        tabSystem = findViewById(R.id.tabSystem);

        tabAll.setOnClickListener(v -> filter("ALL"));
        tabPromo.setOnClickListener(v -> filter("PROMO"));
        tabSystem.setOnClickListener(v -> filter("SYSTEM"));
    }

    private void fetchData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    allList.add(ds.getValue(Notification.class));
                }
                filter("ALL");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filter(String type) {
        List<Notification> filtered = new ArrayList<>();
        for (Notification n : allList) {
            if (type.equals("ALL") || n.getType().equals(type)) filtered.add(n);
        }

        View layoutEmpty = findViewById(R.id.layoutEmptyNoti);

        if (filtered.isEmpty()) {
            rvNoti.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNoti.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

            adapter = new NotificationAdapter(this, filtered, n -> {
                dbRef.child(n.getId()).child("isRead").setValue(true);
            });
            rvNoti.setLayoutManager(new LinearLayoutManager(this));
            rvNoti.setAdapter(adapter);
        }
        updateTabUI(type);
    }

    private void clearOldNotifications() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        for (Notification n : allList) {
            if (n.getTimestamp() < sevenDaysAgo) {
                dbRef.child(n.getId()).removeValue();
            }
        }
        Toast.makeText(this, "Đã dọn dẹp thông báo cũ!", Toast.LENGTH_SHORT).show();
    }

    private void markAllAsRead() {
        for (Notification n : allList) {
            if (!n.isRead()) dbRef.child(n.getId()).child("isRead").setValue(true);
        }
        Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
    }

    private void updateTabUI(String type) {
        int activeColor = ContextCompat.getColor(this, R.color.bg_main);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_secondary);

        // Reset tất cả về tối
        tabAll.setBackgroundResource(0); tabAll.setTextColor(inactiveColor);
        tabPromo.setBackgroundResource(0); tabPromo.setTextColor(inactiveColor);
        tabSystem.setBackgroundResource(0); tabSystem.setTextColor(inactiveColor);

        // Sáng cái được chọn
        if (type.equals("ALL")) {
            tabAll.setBackgroundResource(R.drawable.bg_neon_button);
            tabAll.setTextColor(activeColor);
        } else if (type.equals("PROMO")) {
            tabPromo.setBackgroundResource(R.drawable.bg_neon_button);
            tabPromo.setTextColor(activeColor);
        } else {
            tabSystem.setBackgroundResource(R.drawable.bg_neon_button);
            tabSystem.setTextColor(activeColor);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // 1. Ép thanh menu phải sáng đúng ở mục "Thông báo"
            bottomNavigationView.setSelectedItemId(R.id.nav_notifications);

            // 2. Viết logic chuyển trang cho TẤT CẢ các Tab
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0); // Giúp chuyển trang mượt không bị giật
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(this, MoviesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(this, AiChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    return true; // Đang ở chính nó thì không làm gì cả
                } else if (itemId == R.id.nav_tickets) {
                    startActivity(new Intent(this, MyTicketsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }
}