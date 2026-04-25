package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAiSuggested, rvNowPlaying, rvHotMovies, rvUpcomingMovies;
    private ShapeableImageView imgAvatar;
    private ImageView imgSearch;

    // --- KHAI BÁO CHO BANNER ---
    private ViewPager2 vpBanners;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

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

        // 3. Gọi hàm tải dữ liệu thật từ Firebase thay vì dùng dữ liệu giả
        fetchMoviesFromFirebase();

        // 4. Cài đặt Banner tự động trượt
        setupBanners();

        // 5. Xử lý thanh Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        rvAiSuggested = findViewById(R.id.rvAiSuggested);
        rvNowPlaying = findViewById(R.id.rvNowPlaying);
        rvHotMovies = findViewById(R.id.rvHotMovies);
        rvUpcomingMovies = findViewById(R.id.rvUpcomingMovies);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgSearch = findViewById(R.id.imgSearch);

        // Ánh xạ ViewPager2
        vpBanners = findViewById(R.id.vpBanners);
    }

    // ==========================================
    // LOGIC TẢI DỮ LIỆU TỪ FIREBASE
    // ==========================================
    private void fetchMoviesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> movieList = new ArrayList<>();

                // Lặp qua tất cả 30 phim trên Firebase
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Movie movie = postSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        // RẤT QUAN TRỌNG: Gán cái Key (phim1, phim2...) vào ID của Movie
                        // để khi click vào, nó biết đường truyền sang MovieDetailActivity
                        movie.setId(postSnapshot.getKey());
                        movieList.add(movie);
                        System.out.println("CINEGO_DEBUG: Đã tải được phim -> " + movie.getTitle());
                    }
                }

                // Cài đặt Adapter cho các thanh cuộn với danh sách phim vừa tải về
                // (Tạm thời mình nhét chung 1 list, bạn có thể tách list ra theo thể loại sau này)
                setupRecyclerView(rvAiSuggested, movieList);

                // Đảo lộn danh sách một chút cho các mục khác nhau nhìn cho đa dạng
                List<Movie> list2 = new ArrayList<>(movieList); Collections.shuffle(list2);
                setupRecyclerView(rvNowPlaying, list2);

                List<Movie> list3 = new ArrayList<>(movieList); Collections.shuffle(list3);
                setupRecyclerView(rvHotMovies, list3);

                List<Movie> list4 = new ArrayList<>(movieList); Collections.shuffle(list4);
                setupRecyclerView(rvUpcomingMovies, list4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải phim: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // LOGIC CÀI ĐẶT BANNER TỰ ĐỘNG TRƯỢT
    // ==========================================
    private void setupBanners() {
        List<Integer> bannerList = new ArrayList<>();
        bannerList.add(R.drawable.img_banner_01);
        bannerList.add(R.drawable.img_banner_02);
        bannerList.add(R.drawable.img_banner_03);
        bannerList.add(R.drawable.img_banner_04);
        bannerList.add(R.drawable.img_banner_05);
        bannerList.add(R.drawable.img_banner_06);

        BannerAdapter bannerAdapter = new BannerAdapter(bannerList);
        vpBanners.setAdapter(bannerAdapter);

        // Đặt vị trí ban đầu ở giữa dãy số vô tận
        vpBanners.setCurrentItem(bannerList.size() * 1000, false);

        vpBanners.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                // Đã đồng bộ thời gian trượt là 3 giây (3000ms) cho mượt mà
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            vpBanners.setCurrentItem(vpBanners.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Đồng bộ 3s
    }
    // ==========================================

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
                startActivity(new Intent(getApplicationContext(), MyTicketsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}