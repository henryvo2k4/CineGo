package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtPromoCode;
    private Button btnApplyPromo, btnConfirmPayment;
    private ConstraintLayout layoutZaloPay, layoutMoMo, layoutCreditCard;
    private TextView tvFinalTotal;

    private int amountFromPrevious = 0;
    private int discount = 0;
    private String selectedMethod = "ZaloPay"; // Mặc định như trong XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();

        // 1. Nhận tổng tiền từ màn hình Bắp Nước truyền sang
        amountFromPrevious = getIntent().getIntExtra("GRAND_TOTAL", 360000);
        updatePriceUI();

        // 2. Xử lý nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // 3. Xử lý chọn Phương thức thanh toán
        setupPaymentSelection();

        // 4. Xử lý áp dụng Mã giảm giá
        btnApplyPromo.setOnClickListener(v -> applyPromoCode());

        // 5. Xử lý nút Xác nhận thanh toán
        btnConfirmPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Đang kết nối với ví " + selectedMethod + "...", Toast.LENGTH_LONG).show();

            // Chuyển sang màn hình Vé điện tử (Thành công)
            Intent intent = new Intent(CheckoutActivity.this, TicketActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        tvFinalTotal = findViewById(R.id.tvFinalTotal); // Lưu ý: Hãy thêm ID này vào TextView hiển thị 240.000đ ở dưới cùng trong XML

        layoutZaloPay = findViewById(R.id.layoutZaloPay);
        layoutMoMo = findViewById(R.id.layoutMoMo);
        layoutCreditCard = findViewById(R.id.layoutCreditCard);
    }

    private void setupPaymentSelection() {
        layoutZaloPay.setOnClickListener(v -> {
            selectedMethod = "ZaloPay";
            Toast.makeText(this, "Đã chọn ZaloPay", Toast.LENGTH_SHORT).show();
            // (Thực tế: Code logic thay đổi màu viền/checkbox tại đây)
        });

        layoutMoMo.setOnClickListener(v -> {
            selectedMethod = "MoMo";
            Toast.makeText(this, "Đã chọn MoMo", Toast.LENGTH_SHORT).show();
        });

        layoutCreditCard.setOnClickListener(v -> {
            selectedMethod = "Thẻ ngân hàng";
            Toast.makeText(this, "Đã chọn Thẻ Visa/Mastercard", Toast.LENGTH_SHORT).show();
        });
    }

    private void applyPromoCode() {
        String code = edtPromoCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Vui lòng nhập mã", Toast.LENGTH_SHORT).show();
            return;
        }

        // Giả lập kiểm tra mã (Ví dụ mã CINEGO20 giảm 20k)
        if (code.equalsIgnoreCase("CINEGO20")) {
            discount = 20000;
            updatePriceUI();
            Toast.makeText(this, "Đã áp dụng mã giảm 20.000đ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mã không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePriceUI() {
        int total = amountFromPrevious - discount;
        if (tvFinalTotal != null) {
            tvFinalTotal.setText(String.format("%,d đ", total));
        }
    }
}