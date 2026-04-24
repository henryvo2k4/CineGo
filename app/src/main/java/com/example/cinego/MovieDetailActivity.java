package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgBackdrop, btnPlayTrailer, btnFavorite;
    private TextView tvMovieTitle, tvSynopsis;
    private Button btnBookTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // 1. Ánh xạ các View từ XML
        initViews();

        // 2. Nhận dữ liệu từ Intent (nếu có truyền từ trang trước)
        // Hiện tại MovieAdapter đang chuyển trang đơn giản,
        // bạn có thể mở rộng để nhận ID phim và đổ dữ liệu thật tại đây.

        // 3. Xử lý nút Quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình này để quay lại trang trước
            }
        });

        // 4. Xử lý nút Đặt vé ngay
        btnBookTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình Chọn lịch chiếu
                Intent intent = new Intent(MovieDetailActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });

        // 5. Các tính năng phụ (Giả lập)
        btnPlayTrailer.setOnClickListener(v ->
                Toast.makeText(this, "Đang mở Trailer...", Toast.LENGTH_SHORT).show());

        btnFavorite.setOnClickListener(v ->
                Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgBackdrop = findViewById(R.id.imgBackdrop);
        btnPlayTrailer = findViewById(R.id.btnPlayTrailer);
        btnFavorite = findViewById(R.id.btnFavorite);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvSynopsis = findViewById(R.id.tvSynopsis);
        btnBookTicket = findViewById(R.id.btnBookTicket);
    }
}