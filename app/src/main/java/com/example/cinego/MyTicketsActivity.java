package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvTickets;
    private TextView tabUpcoming, tabHistory;

    private TicketAdapter ticketAdapter;
    private List<Ticket> upcomingList;
    private List<Ticket> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // 1. Ánh xạ View
        initViews();

        // 2. Xử lý nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Chuẩn bị dữ liệu cho 2 Tab
        prepareData();

        // 4. Thiết lập RecyclerView (Mặc định hiển thị vé Sắp tới)
        setupRecyclerView();

        // 5. Thiết lập logic bấm chuyển Tab
        setupTabsLogic();

        // 6. Kích hoạt Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvMyTickets);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabHistory = findViewById(R.id.tabHistory);
    }

    private void prepareData() {
        // Dữ liệu giả lập cho Tab "Sắp tới" (Vé chưa xem)
        upcomingList = new ArrayList<>();
        upcomingList.add(new Ticket("Avatar: Dòng Chảy Của Nước", "CineGo Landmark 81", "14 Th 10 • 13:15", "M1, M2", R.drawable.img_bg_login));
        upcomingList.add(new Ticket("Kung Fu Panda 4", "CineGo Giga Mall", "20 Th 10 • 19:00", "G4, G5", R.drawable.img_bg_login));

        // Dữ liệu giả lập cho Tab "Lịch sử" (Vé đã xem trong quá khứ)
        historyList = new ArrayList<>();
        historyList.add(new Ticket("Lật Mặt 6: Tấm Vé Định Mệnh", "CineGo Sư Vạn Hạnh", "10 Th 05 • 20:00", "A1, A2, A3", R.drawable.img_bg_login));
        historyList.add(new Ticket("Doraemon: Khủng Long Nobita", "CineGo Aeon Tân Phú", "01 Th 06 • 09:30", "F7, F8", R.drawable.img_bg_login));
        historyList.add(new Ticket("Mai", "CineGo Landmark 81", "14 Th 02 • 18:00", "VIP 1, VIP 2", R.drawable.img_bg_login));
    }

    private void setupRecyclerView() {
        // Mặc định lúc mới vào trang sẽ hiển thị danh sách Upcoming (Sắp tới)
        ticketAdapter = new TicketAdapter(this, upcomingList);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setAdapter(ticketAdapter);
    }

    private void setupTabsLogic() {
        // Bắt sự kiện bấm vào Tab "Sắp tới"
        tabUpcoming.setOnClickListener(v -> {
            // Đổi giao diện: Nổi bật Tab Sắp tới
            tabUpcoming.setBackgroundResource(R.drawable.bg_neon_button);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.bg_main));

            // Làm mờ Tab Lịch sử (Xóa background, đổi chữ thành màu xám)
            tabHistory.setBackgroundResource(0);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Cập nhật lại danh sách bên dưới
            ticketAdapter = new TicketAdapter(this, upcomingList);
            rvTickets.setAdapter(ticketAdapter);
        });

        // Bắt sự kiện bấm vào Tab "Lịch sử"
        tabHistory.setOnClickListener(v -> {
            // Đổi giao diện: Nổi bật Tab Lịch sử
            tabHistory.setBackgroundResource(R.drawable.bg_neon_button);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.bg_main));

            // Làm mờ Tab Sắp tới
            tabUpcoming.setBackgroundResource(0);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Cập nhật lại danh sách bên dưới
            ticketAdapter = new TicketAdapter(this, historyList);
            rvTickets.setAdapter(ticketAdapter);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tickets);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(getApplicationContext(), MoviesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(getApplicationContext(), AiChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_tickets) {
                    return true;
                }
                return false;
            });
        }
    }
}