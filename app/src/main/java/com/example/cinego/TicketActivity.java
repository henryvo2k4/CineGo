package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TicketActivity extends AppCompatActivity {

    private ImageView btnClose;
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // 1. Ánh xạ các View từ XML
        btnClose = findViewById(R.id.btnClose);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // 2. Xử lý nút Đóng (dấu X)
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        // 3. Xử lý nút "Về Trang Chủ"
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        // Gợi ý: Bạn có thể nhận dữ liệu vé từ CheckoutActivity truyền sang
        // để hiển thị đúng tên phim, phòng chiếu và số ghế thực tế tại đây.
    }

    /**
     * Hàm dùng để quay lại màn hình chính và xóa các màn hình trung gian (Thanh toán, Chọn ghế...)
     * ra khỏi bộ nhớ (Stack) để app chạy nhẹ và đúng luồng.
     */
    private void backToHome() {
        Intent intent = new Intent(TicketActivity.this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TOP giúp xóa hết các Activity cũ, đưa MainActivity lên trên cùng
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Đóng màn hình hiện tại

        Toast.makeText(this, "Chúc bạn xem phim vui vẻ!", Toast.LENGTH_SHORT).show();
    }
}