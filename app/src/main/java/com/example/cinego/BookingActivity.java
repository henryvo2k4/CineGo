package com.example.cinego;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {

    // Khai báo các biến cho Ngày
    private LinearLayout btnDate1, btnDate2, btnDate3;
    private TextView tvDay1, tvDate1, tvDay2, tvDate2, tvDay3, tvDate3;

    // Khai báo các biến cho Giờ
    private TextView btnTime1, btnTime2, btnTime3;

    // Khai báo các biến cho Rạp
    private View btnCinema1, btnCinema2, barCinema1, barCinema2;
    private TextView tvCinemaName1, tvCinemaName2;

    // CÁC BIẾN LƯU TRỮ LỰA CHỌN (Để truyền sang màn hình sau)
    private String selectedDate = "Hôm nay, 22"; // Mặc định chọn ngày đầu
    private String selectedTime = "13:15";       // Mặc định chọn giờ giữa
    private String selectedCinema = "CineGo Landmark 81";
    private String movieName = "";
    private String posterUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Nhận thông tin phim từ màn hình trước (Trang chủ hoặc Chi tiết)
        movieName = getIntent().getStringExtra("movieName");
        posterUrl = getIntent().getStringExtra("posterUrl");

        anhXaView();
        setupClickListeners();

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nút chuyển sang chọn ghế
        findViewById(R.id.btnNextToSeat).setOnClickListener(v -> {
            Intent intent = new Intent(this, SeatActivity.class);

            // Đóng gói tất cả thông tin để gửi sang SeatActivity
            intent.putExtra("movieName", movieName);
            intent.putExtra("posterUrl", posterUrl);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("selectedTime", selectedTime);
            intent.putExtra("selectedCinema", selectedCinema);

            startActivity(intent);
        });
    }

    private void anhXaView() {
        // Ngày
        btnDate1 = findViewById(R.id.btnDate1);
        btnDate2 = findViewById(R.id.btnDate2);
        btnDate3 = findViewById(R.id.btnDate3);
        tvDay1 = findViewById(R.id.tvDay1);
        tvDate1 = findViewById(R.id.tvDate1);
        tvDay2 = findViewById(R.id.tvDay2);
        tvDate2 = findViewById(R.id.tvDate2);
        tvDay3 = findViewById(R.id.tvDay3);
        tvDate3 = findViewById(R.id.tvDate3);

        // Giờ
        btnTime1 = findViewById(R.id.btnTime1);
        btnTime2 = findViewById(R.id.btnTime2);
        btnTime3 = findViewById(R.id.btnTime3);

        // Rạp
        btnCinema1 = findViewById(R.id.btnCinema1);
        btnCinema2 = findViewById(R.id.btnCinema2);
        barCinema1 = findViewById(R.id.barCinema1);
        barCinema2 = findViewById(R.id.barCinema2);
        tvCinemaName1 = findViewById(R.id.tvCinemaName1);
        tvCinemaName2 = findViewById(R.id.tvCinemaName2);
    }

    private void setupClickListeners() {
        // Xử lý chọn Ngày
        View.OnClickListener dateClick = v -> {
            resetDates();
            v.setBackgroundResource(R.drawable.bg_neon_button);
            if (v.getId() == R.id.btnDate1) {
                selectedDate = "Hôm nay, 22";
                tvDay1.setTextColor(Color.parseColor("#0B0C10"));
                tvDate1.setTextColor(Color.parseColor("#0B0C10"));
            } else if (v.getId() == R.id.btnDate2) {
                selectedDate = "Thứ 7, 23";
                tvDay2.setTextColor(Color.parseColor("#0B0C10"));
                tvDate2.setTextColor(Color.parseColor("#0B0C10"));
            } else if (v.getId() == R.id.btnDate3) {
                selectedDate = "Chủ nhật, 24";
                tvDay3.setTextColor(Color.parseColor("#0B0C10"));
                tvDate3.setTextColor(Color.parseColor("#0B0C10"));
            }
        };
        btnDate1.setOnClickListener(dateClick);
        btnDate2.setOnClickListener(dateClick);
        btnDate3.setOnClickListener(dateClick);

        // Xử lý chọn Giờ
        View.OnClickListener timeClick = v -> {
            resetTimes();
            v.setBackgroundResource(R.drawable.bg_neon_button);
            TextView tv = (TextView) v;
            tv.setTextColor(Color.parseColor("#0B0C10"));
            selectedTime = tv.getText().toString(); // Lưu lại giờ đã chọn
        };
        btnTime1.setOnClickListener(timeClick);
        btnTime2.setOnClickListener(timeClick);
        btnTime3.setOnClickListener(timeClick);

        // Xử lý chọn Rạp
        View.OnClickListener cinemaClick = v -> {
            resetCinemas();
            v.setBackgroundResource(R.drawable.bg_card_3d);
            if (v.getId() == R.id.btnCinema1) {
                selectedCinema = "CineGo Landmark 81";
                barCinema1.setVisibility(View.VISIBLE);
                tvCinemaName1.setTextColor(Color.parseColor("#66FCF1"));
            } else if (v.getId() == R.id.btnCinema2) {
                selectedCinema = "CineGo GigaMall";
                barCinema2.setVisibility(View.VISIBLE);
                tvCinemaName2.setTextColor(Color.parseColor("#66FCF1"));
            }
        };
        btnCinema1.setOnClickListener(cinemaClick);
        btnCinema2.setOnClickListener(cinemaClick);
    }

    private void resetDates() {
        btnDate1.setBackgroundResource(R.drawable.bg_input);
        btnDate2.setBackgroundResource(R.drawable.bg_input);
        btnDate3.setBackgroundResource(R.drawable.bg_input);
        int colorNormal = Color.parseColor("#C5C6C7");
        tvDay1.setTextColor(colorNormal);
        tvDate1.setTextColor(Color.WHITE);
        tvDay2.setTextColor(colorNormal);
        tvDate2.setTextColor(Color.WHITE);
        tvDay3.setTextColor(colorNormal);
        tvDate3.setTextColor(Color.WHITE);
    }

    private void resetTimes() {
        btnTime1.setBackgroundResource(R.drawable.bg_input);
        btnTime2.setBackgroundResource(R.drawable.bg_input);
        btnTime3.setBackgroundResource(R.drawable.bg_input);
        btnTime1.setTextColor(Color.WHITE);
        btnTime2.setTextColor(Color.WHITE);
        btnTime3.setTextColor(Color.WHITE);
    }

    private void resetCinemas() {
        btnCinema1.setBackgroundResource(R.drawable.bg_input);
        btnCinema2.setBackgroundResource(R.drawable.bg_input);
        barCinema1.setVisibility(View.INVISIBLE);
        barCinema2.setVisibility(View.INVISIBLE);
        tvCinemaName1.setTextColor(Color.WHITE);
        tvCinemaName2.setTextColor(Color.WHITE);
    }
}