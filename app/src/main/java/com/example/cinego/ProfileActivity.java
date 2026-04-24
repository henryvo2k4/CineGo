package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private AppCompatButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file activity_profile.xml
        setContentView(R.layout.activity_profile);

        // 1. Ánh xạ View chính xác theo ID trong XML
        btnLogout = findViewById(R.id.btnLogout);

        // 2. Xử lý sự kiện khi bấm nút "Đăng xuất"
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // (Trong thực tế, ở đây sẽ có code xóa SharedPreferences hoặc đăng xuất Firebase)

                Toast.makeText(ProfileActivity.this, "Hẹn gặp lại bạn nhé!", Toast.LENGTH_SHORT).show();

                // Chuyển người dùng về màn hình Đăng nhập (LoginActivity)
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                // FLAG_ACTIVITY_NEW_TASK và CLEAR_TASK:
                // Xóa sổ toàn bộ các màn hình trước đó (Home, Chat, Profile...) khỏi bộ nhớ.
                // Giúp người dùng không thể bấm phím Back trên điện thoại để lọt vào lại app.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });

        // 3. Xử lý thanh điều hướng Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // Đặt sáng icon tương ứng (Tạm dùng nav_tickets cho tab Profile cuối cùng)
            bottomNavigationView.setSelectedItemId(R.id.nav_tickets);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    // Chờ code file MoviesActivity
                    // startActivity(new Intent(getApplicationContext(), MoviesActivity.class));
                    // overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(getApplicationContext(), AiChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    // Chờ code file NotificationsActivity
                    // startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                    // overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_tickets) {
                    // Đang ở trang hiện tại rồi thì không làm gì cả
                    return true;
                }
                return false;
            });
        }
    }
}