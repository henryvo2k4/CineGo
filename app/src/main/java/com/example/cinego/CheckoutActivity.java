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
            String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            if (currentUserId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Kết nối Firebase để lưu vé dưới ID người dùng
            DatabaseReference dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("booked_tickets").child(currentUserId);

            String ticketId = dbRef.push().getKey();
            String posterUrl = getIntent().getStringExtra("posterUrl");

            // Tạo đối tượng vé để lưu
            Ticket ticket = new Ticket(
                    ticketId,
                    tvMovieName.getText().toString(),
                    tvCinema.getText().toString(),
                    tvDateTime.getText().toString(),
                    tvSeats.getText().toString(),
                    tvSnacksName.getText().toString(),
                    tvFinalTotal.getText().toString(),
                    posterUrl,
                    System.currentTimeMillis()
            );

            if (ticketId != null) {
                dbRef.child(ticketId).setValue(ticket).addOnSuccessListener(aVoid -> {

                    // --- 2. TỰ ĐỘNG TẠO THÔNG BÁO HỆ THỐNG CHO NGƯỜI DÙNG NÀY ---
                    DatabaseReference notiRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("notifications").child(currentUserId);

                    String notiId = notiRef.push().getKey();
                    if (notiId != null) {
                        Notification bookingNoti = new Notification(
                                notiId,
                                "Đặt vé thành công! 🎉",
                                "Bạn đã đặt thành công vé phim " + tvMovieName.getText().toString() + ". Xem chi tiết tại mục Vé của tôi nhé!",
                                "SYSTEM",
                                System.currentTimeMillis(),
                                false
                        );
                        notiRef.child(notiId).setValue(bookingNoti);
                    }

                    // --- 3. HIỂN THỊ THÀNH CÔNG VÀ CHUYỂN SANG MÀN HÌNH VÉ (TICKET ACTIVITY) ---
                    Toast.makeText(this, "Thanh toán thành công qua " + selectedMethod, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, TicketActivity.class);

                    // Truyền toàn bộ thông tin sang để hiện lên cái vé điện tử
                    intent.putExtra("movieName", tvMovieName.getText().toString());
                    intent.putExtra("selectedCinema", tvCinema.getText().toString());

                    // Tách Ngày và Giờ từ chuỗi "Ngày • Giờ" để hiển thị cho đẹp
                    String fullDateTime = tvDateTime.getText().toString();
                    if (fullDateTime.contains(" • ")) {
                        intent.putExtra("selectedDate", fullDateTime.split(" • ")[0]);
                        intent.putExtra("selectedTime", fullDateTime.split(" • ")[1]);
                    } else {
                        intent.putExtra("selectedDate", fullDateTime);
                        intent.putExtra("selectedTime", "");
                    }

                    intent.putExtra("seats", tvSeats.getText().toString());
                    intent.putExtra("snackDetails", tvSnacksName.getText().toString());
                    intent.putExtra("posterUrl", posterUrl);
                    intent.putExtra("ticketId", ticketId); // Để hiện mã vé TKT-XXXX

                    startActivity(intent);
                    finish(); // Đóng màn hình thanh toán lại

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