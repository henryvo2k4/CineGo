package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvMovieName, tvCinema, tvDateTime, tvSeats, tvSnacksName;
    private TextView tvTempTicket, tvSnackPrice, tvDiscount, tvFinalTotal;
    private View layoutZalo, layoutMoMo, layoutCard;
    private ImageView imgPoster;

    private long ticketPrice = 0, snackPrice = 0, finalTotal = 0;
    private String selectedMethod = "ZaloPay"; // Mặc định chọn ZaloPay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        anhXaView();
        nhanVaHienThiData();
        setupPaymentSelection(); // Thêm hàm xử lý chọn phương thức thanh toán

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Áp dụng mã giảm giá
        findViewById(R.id.btnApplyPromo).setOnClickListener(v -> {
            EditText edt = findViewById(R.id.edtPromoCode);
            if (edt.getText().toString().equalsIgnoreCase("CINEGO")) {
                long discount = 20000;
                tvDiscount.setText("- " + String.format("%,d đ", discount));
                tvFinalTotal.setText(String.format("%,d đ", finalTotal - discount));
                Toast.makeText(this, "Đã giảm 20k!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xác nhận thanh toán
        findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            // 1. Kết nối tới Firebase
            DatabaseReference dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("booked_tickets");

            // 2. Tạo một ID duy nhất cho vé này
            String ticketId = dbRef.push().getKey();

            // 3. Lấy dữ liệu từ giao diện để đóng gói (Dùng các biến bạn đã nạp data lúc nãy)
            // Lưu ý: Lấy lại link poster từ Intent để lưu vào vé
            String posterUrl = getIntent().getStringExtra("posterUrl");

            Ticket ticket = new Ticket(
                    ticketId,
                    tvMovieName.getText().toString(),
                    tvCinema.getText().toString(),
                    tvDateTime.getText().toString(),
                    tvSeats.getText().toString(),
                    tvSnacksName.getText().toString(),
                    tvFinalTotal.getText().toString(),
                    posterUrl,
                    System.currentTimeMillis() // Lưu thời gian hiện tại để biết vé mới hay cũ
            );

            // 4. Đẩy dữ liệu lên Firebase
            if (ticketId != null) {
                dbRef.child(ticketId).setValue(ticket).addOnSuccessListener(aVoid -> {
                    // Chỉ khi lưu xong mới báo thành công và chuyển trang
                    Toast.makeText(this, "Thanh toán thành công qua " + selectedMethod, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các màn hình cũ cho nhẹ máy
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu vé: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void anhXaView() {
        tvMovieName = findViewById(R.id.tvMovieNameCheckout);
        tvCinema = findViewById(R.id.tvCinemaCheckout);
        tvDateTime = findViewById(R.id.tvDateTimeCheckout);
        tvSeats = findViewById(R.id.tvSeatsCheckout);
        tvSnacksName = findViewById(R.id.tvSnacksNameCheckout);

        tvTempTicket = findViewById(R.id.tvTotalPrice);
        tvSnackPrice = findViewById(R.id.tvSnackPriceCheckout);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        imgPoster = findViewById(R.id.imgMoviePosterCheckout);

        // Ánh xạ các khung thanh toán
        layoutZalo = findViewById(R.id.layoutZaloPay);
        layoutMoMo = findViewById(R.id.layoutMoMo);
        layoutCard = findViewById(R.id.layoutCreditCard);
    }

    private void setupPaymentSelection() {
        View.OnClickListener paymentListener = v -> {
            // 1. Reset tất cả về màu tối (Chưa chọn)
            layoutZalo.setBackgroundResource(R.drawable.bg_input);
            layoutMoMo.setBackgroundResource(R.drawable.bg_input);
            layoutCard.setBackgroundResource(R.drawable.bg_input);

            // 2. Bật màu sáng (bg_card_3d) cho cái vừa được nhấn
            v.setBackgroundResource(R.drawable.bg_card_3d);

            // 3. Lưu lại phương thức đã chọn
            if (v.getId() == R.id.layoutZaloPay) selectedMethod = "ZaloPay";
            else if (v.getId() == R.id.layoutMoMo) selectedMethod = "MoMo";
            else if (v.getId() == R.id.layoutCreditCard) selectedMethod = "Thẻ ngân hàng";
        };

        layoutZalo.setOnClickListener(paymentListener);
        layoutMoMo.setOnClickListener(paymentListener);
        layoutCard.setOnClickListener(paymentListener);
    }

    private void nhanVaHienThiData() {
        Intent it = getIntent();

        ticketPrice = it.getLongExtra("totalPrice", 0);
        snackPrice = it.getLongExtra("snackPrice", 0);
        String snackDetails = it.getStringExtra("snackDetails");
        finalTotal = ticketPrice + snackPrice;

        String name = it.getStringExtra("movieName");
        String cinema = it.getStringExtra("selectedCinema");
        String date = it.getStringExtra("selectedDate");
        String time = it.getStringExtra("selectedTime");
        ArrayList<String> seats = it.getStringArrayListExtra("selectedSeats");
        String poster = it.getStringExtra("posterUrl");

        // Sửa lại trong CheckoutActivity.java
        if (name != null) {
            tvMovieName.setText(name);
        } else {
            tvMovieName.setText("Lỗi: Không nhận được tên phim");
            // Nếu app hiện dòng này, nghĩa là bạn quên gửi movieName ở Seat hoặc Snacks
        }
        tvCinema.setText(cinema);
        tvDateTime.setText(date + " • " + time);
        if (seats != null) tvSeats.setText("Ghế: " + android.text.TextUtils.join(", ", seats));
        tvSnacksName.setText("Combo: " + (snackDetails != null ? snackDetails : "Không mua"));

        tvTempTicket.setText(String.format("%,d đ", ticketPrice));
        tvSnackPrice.setText(String.format("%,d đ", snackPrice));
        tvFinalTotal.setText(String.format("%,d đ", finalTotal));

        if (poster != null && !poster.isEmpty()) {
            Glide.with(this).load(poster).into(imgPoster);
        }
    }
}