package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // 1. Ánh xạ View
        rvMovies = findViewById(R.id.rvAllMovies);

        // 2. Kéo dữ liệu từ Firebase về thay vì dùng dữ liệu giả
        if (rvMovies != null) {
            fetchMoviesFromFirebase();
        }

        // 3. Xử lý thanh Menu Bottom Navigation
        setupBottomNavigation();
    }

    private void fetchMoviesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> list = new ArrayList<>();

                // Lấy toàn bộ danh sách phim từ Database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Movie movie = postSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        // Bắt buộc: Gắn Key (ID) để khi bấm vào phim, trang Chi tiết mới biết là phim nào
                        movie.setId(postSnapshot.getKey());
                        list.add(movie);
                    }
                }

                // Dùng GridLayoutManager để chia danh sách thành một "lưới" gồm 2 cột
                rvMovies.setLayoutManager(new GridLayoutManager(MoviesActivity.this, 2));

                // Tái sử dụng lại Adapter đã tạo từ bài Trang chủ
                movieAdapter = new MovieAdapter(MoviesActivity.this, list);
                rvMovies.setAdapter(movieAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoviesActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_movies);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    return true; // Đang ở trang hiện tại
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
}