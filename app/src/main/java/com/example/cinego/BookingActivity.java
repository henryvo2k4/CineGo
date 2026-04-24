package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnNextToSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. Ánh xạ các View từ XML
        btnBack = findViewById(R.id.btnBack);
        btnNextToSeat = findViewById(R.id.btnNextToSeat);

        // 2. Xử lý nút Quay lại (Back)
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình Chọn lịch chiếu, quay về Chi tiết phim
            }
        });

        // 3. Xử lý nút Tiếp tục Chọn Ghế
        btnNextToSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giả lập việc người dùng đã chọn xong lịch
                Toast.makeText(BookingActivity.this, "Đã chốt lịch chiếu!", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình Chọn Ghế (SeatActivity)
                Intent intent = new Intent(BookingActivity.this, SeatActivity.class);
                startActivity(intent);
            }
        });
    }
}