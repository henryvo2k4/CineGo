package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity {

    // Khai báo RecyclerView để hiển thị danh sách
    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nạp file giao diện tương ứng (Giả định bạn đặt tên là activity_movies.xml)
        setContentView(R.layout.activity_movies);

        // 1. Ánh xạ View (Đã cập nhật ID khớp với file XML là rvAllMovies)
        rvMovies = findViewById(R.id.rvAllMovies);

        // 2. Thiết lập hiển thị danh sách
        if (rvMovies != null) {
            setupRecyclerView();
        }

        // 3. Xử lý thanh Menu Bottom Navigation
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        // Tạo dữ liệu giả lập (Lần này tạo nhiều phim hơn để xem cuộn dọc)
        List<Movie> list = new ArrayList<>();
        list.add(new Movie("Avatar: Dòng Chảy Của Nước", R.drawable.img_bg_login, 8.5, "Viễn tưởng"));
        list.add(new Movie("Lật Mặt 6: Tấm Vé Định Mệnh", R.drawable.img_bg_login, 7.8, "Hành động"));
        list.add(new Movie("Doraemon: Khủng Long Nobita", R.drawable.img_bg_login, 9.0, "Hoạt hình"));
        list.add(new Movie("Quỷ Ám: Tín Đồ", R.drawable.img_bg_login, 6.5, "Kinh dị"));
        list.add(new Movie("Avengers: Endgame", R.drawable.img_bg_login, 8.9, "Hành động"));
        list.add(new Movie("Nhà Bà Nữ", R.drawable.img_bg_login, 7.2, "Tâm lý"));
        list.add(new Movie("Spider-Man: No Way Home", R.drawable.img_bg_login, 8.8, "Hành động"));
        list.add(new Movie("Oppenheimer", R.drawable.img_bg_login, 8.6, "Tiểu sử"));

        // Dùng GridLayoutManager để chia danh sách thành một "lưới" gồm 2 cột
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));

        // Tái sử dụng lại Adapter đã tạo từ bài Trang chủ
        movieAdapter = new MovieAdapter(this, list);
        rvMovies.setAdapter(movieAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // Cập nhật trạng thái icon: Đặt sáng icon "Phim"
            bottomNavigationView.setSelectedItemId(R.id.nav_movies);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    return true; // Đang ở trang hiện tại, không làm gì cả
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
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }
}
