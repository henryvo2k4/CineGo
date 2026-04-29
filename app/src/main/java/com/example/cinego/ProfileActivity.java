package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvDob, tvPhone, tvWatchedMoviesCount, tvBoughtTicketsCount;
    private AppCompatButton btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users").child(currentUser.getUid());
        }

        anhXaView();
        loadUserInfo();

        // 2. Xử lý nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Lệnh đăng xuất của Firebase
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // Quay lại màn hình Login và xóa lịch sử các trang trước
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void anhXaView() {
        tvName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDob = findViewById(R.id.tvDob);
        tvPhone = findViewById(R.id.tvPhone);
        tvWatchedMoviesCount = findViewById(R.id.tvWatchedMoviesCount);
        tvBoughtTicketsCount = findViewById(R.id.tvBoughtTicketsCount);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserInfo() {
        if (dbRef == null) return;

        // 1. Lấy thông tin cơ bản
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvName.setText(user.getFullName());
                        tvEmail.setText("📧 " + user.getEmail());
                        tvDob.setText("📅 " + user.getDob());
                        tvPhone.setText("📞 " + user.getPhone());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Lấy số lượng vé đã mua
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference ticketsRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("booked_tickets").child(userId);

            ticketsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    tvBoughtTicketsCount.setText(String.valueOf(count));
                    tvWatchedMoviesCount.setText(String.valueOf(count)); // Tạm thời lấy bằng số vé
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}