package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SnacksActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvSkip;
    private Button btnContinueToCheckout;

    // Biến lưu trữ tiền
    private int ticketPrice = 0;
    private int snacksPrice = 120000; // Giả sử người dùng đang chọn sẵn 1 Combo VIP (120k) như trong thiết kế XML
    private int grandTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snacks);

        // 1. Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        btnContinueToCheckout = findViewById(R.id.btnContinueToCheckout);

        // Lưu ý: Trong XML của bạn, chữ "Bỏ qua" chưa có ID,
        // bạn có thể thêm android:id="@+id/tvSkip" vào file activity_snacks.xml để nó hoạt động nhé!
        // tvSkip = findViewById(R.id.tvSkip);

        // 2. Nhận tiền vé từ SeatActivity truyền sang
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TOTAL_TICKET_PRICE")) {
            ticketPrice = intent.getIntExtra("TOTAL_TICKET_PRICE", 0);
        } else {
            // Dữ liệu giả lập nếu chạy thẳng màn hình này
            ticketPrice = 240000;
        }

        // 3. Tính tổng tiền (Vé + Bắp)
        grandTotal = ticketPrice + snacksPrice;

        // 4. Xử lý nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // 5. Xử lý nút "Bỏ qua" (Không mua bắp nước)
        /*
        tvSkip.setOnClickListener(v -> {
            grandTotal = ticketPrice; // Chỉ tính tiền vé
            goToCheckout();
        });
        */

        // 6. Xử lý nút Thanh Toán
        btnContinueToCheckout.setOnClickListener(v -> goToCheckout());

        // (Trong thực tế, ở đây sẽ có thêm logic bấm nút [+] [-] để cộng trừ tiền Combo)
    }

    private void goToCheckout() {
        Toast.makeText(this, "Đang chuyển đến trang thanh toán...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SnacksActivity.this, CheckoutActivity.class);
        // Truyền tổng tiền cuối cùng sang màn hình Checkout
        intent.putExtra("GRAND_TOTAL", grandTotal);
        startActivity(intent);
    }
}