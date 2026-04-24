package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotificationsActivity extends AppCompatActivity {

    private ImageView btnBack, btnReadAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // 1. Ánh xạ các View từ XML
        btnBack = findViewById(R.id.btnBack);
        btnReadAll = findViewById(R.id.btnReadAll);

        // 2. Xử lý nút Quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // 3. Xử lý nút "Đánh dấu đã đọc tất cả"
        if (btnReadAll != null) {
            btnReadAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NotificationsActivity.this, "Đã đánh dấu đọc tất cả thông báo", Toast.LENGTH_SHORT).show();
                    // (Thực tế: Code logic đổi màu nền các thông báo chưa đọc thành đã đọc tại đây)
                }
            });
        }

        // 4. Xử lý thanh Menu Bottom Navigation (Nếu có)
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Kiểm tra xem trong XML có BottomNavigation không để tránh lỗi ứng dụng (Crash)
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_notifications);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(getApplicationContext(), MoviesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(getApplicationContext(), AiChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    return true; // Đang ở trang hiện tại
                } else if (itemId == R.id.nav_tickets) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }
}