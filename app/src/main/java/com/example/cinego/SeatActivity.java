package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SeatActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnCheckout;
    private TextView tvTotalPrice, tvSelectedSeatsInfo;

    private int totalPrice = 0;
    private List<String> selectedSeats = new ArrayList<>();

    // Giá vé giả định
    private final int PRICE_NORMAL = 60000;
    private final int PRICE_VIP = 90000;
    private final int PRICE_DOUBLE = 150000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);

        initViews();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Thanh toán
        btnCheckout.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SeatActivity.this, SnacksActivity.class);
                // Truyền tổng tiền sang màn hình bắp nước
                intent.putExtra("TOTAL_TICKET_PRICE", totalPrice);
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện click cho tất cả các ghế trong Grid
        setupSeatClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSelectedSeatsInfo = findViewById(R.id.tvSelectedSeatsInfo);
    }

    private void setupSeatClickListeners() {
        // Trong thực tế, bạn sẽ dùng RecyclerView để quản lý hàng trăm ghế.
        // Ở đây, vì chúng ta dùng GridLayout cố định, ta sẽ tìm các View theo ID.
        // Ví dụ xử lý cho một vài ghế tiêu biểu (A1, B3, M1...):

        int[] seatIds = {R.id.seatA1, R.id.seatA2, R.id.seatA3, R.id.seatA4, R.id.seatA5, R.id.seatA6,
                         R.id.seatB1, R.id.seatB2, R.id.seatB3, R.id.seatB4, R.id.seatB5, R.id.seatB6,
                         R.id.seatM1, R.id.seatM2, R.id.seatM3};

        for (int id : seatIds) {
            View seat = findViewById(id);
            if (seat != null) {
                seat.setOnClickListener(v -> toggleSeatSelection(v, id));
            }
        }
    }

    private void toggleSeatSelection(View view, int seatId) {
        // Logic:
        // 1. Kiểm tra xem ghế đã chọn chưa (dựa trên danh sách selectedSeats)
        // 2. Nếu chưa: Đổi màu nền sang Neon Cyan, thêm vào danh sách, cộng tiền.
        // 3. Nếu rồi: Đổi lại màu cũ, xóa khỏi danh sách, trừ tiền.

        // Tạm thời chỉ demo việc update UI
        updateUI();
    }

    private void updateUI() {
        tvTotalPrice.setText(String.format("%,d VNĐ", totalPrice));
        tvSelectedSeatsInfo.setText(String.format("Tổng cộng (%d ghế)", selectedSeats.size()));
    }
}
