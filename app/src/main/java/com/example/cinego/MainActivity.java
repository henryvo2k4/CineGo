package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAiSuggested, rvNowPlaying, rvHotMovies, rvUpcomingMovies;
    private ShapeableImageView imgAvatar;
    private ImageView imgSearch;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ các View
        initViews();

        // 2. Xử lý sự kiện chuyển trang ở Header
        imgAvatar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        imgSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        // 3. (Tạm thời) Tạo dữ liệu giả. Sau này sẽ thay bằng hàm tải từ Firebase + AI
        List<Movie> movieList = createDummyMovies();

        // 4. Cài đặt Adapter cho các thanh cuộn
        setupRecyclerView(rvAiSuggested, movieList);
        setupRecyclerView(rvNowPlaying, movieList);
        setupRecyclerView(rvHotMovies, movieList);
        setupRecyclerView(rvUpcomingMovies, movieList);

        // 5. Xử lý thanh Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        rvAiSuggested = findViewById(R.id.rvAiSuggested);
        rvNowPlaying = findViewById(R.id.rvNowPlaying);
        rvHotMovies = findViewById(R.id.rvHotMovies);
        rvUpcomingMovies = findViewById(R.id.rvUpcomingMovies);

        // Ánh xạ Avatar và Nút Search từ activity_main.xml
        imgAvatar = findViewById(R.id.imgAvatar);
        imgSearch = findViewById(R.id.imgSearch);
    }

    private List<Movie> createDummyMovies() {
        List<Movie> list = new ArrayList<>();
        list.add(new Movie("Avatar: Dòng Chảy Của Nước", "https://m.media-amazon.com/images/M/MV5BYjhiNjBlODctY2ZiOC00YjVlLWFlNzAtNTVhNzM1YjI1NzMxXkEyXkFqcGdeQXVyMjQxNTE1MDA@._V1_FMjpg_UX1000_.jpg", 8.5, "Viễn tưởng"));
        list.add(new Movie("Lật Mặt 6: Tấm Vé Định Mệnh", "https://upload.wikimedia.org/wikipedia/vi/a/a2/L%E1%BA%ADt_m%E1%BA%B7t_6_-_T%E1%BA%A5m_v%C3%A9_%C4%91%E1%BB%8Bnh_m%E1%BB%87nh_poster.jpg", 7.8, "Hành động"));
        list.add(new Movie("Doraemon: Khủng Long Nobita", "https://m.media-amazon.com/images/M/MV5BNzQ4Yjc5MDMtNWRkOS00YWE0LWFkNzEtZmU1YWMyZmIxZTU4XkEyXkFqcGdeQXVyMjg0MTI5NzQ@._V1_.jpg", 9.0, "Hoạt hình"));
        return list;
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Movie> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        MovieAdapter adapter = new MovieAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
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
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_tickets) {
                // Sửa lại thành nhảy sang trang Profile nếu "Vé của tôi" gộp chung
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}