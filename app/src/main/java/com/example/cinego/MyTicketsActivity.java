package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvTickets;
    private TextView tabUpcoming, tabHistory;

    private TicketAdapter ticketAdapter;
    private List<Ticket> allTicketsList = new ArrayList<>();
    private List<Ticket> upcomingList = new ArrayList<>();
    private List<Ticket> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        initViews();
        setupBottomNavigation();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Lấy dữ liệu THẬT từ Firebase
        fetchTicketsFromFirebase();

        // Thiết lập logic bấm chuyển Tab
        setupTabsLogic();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvMyTickets);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabHistory = findViewById(R.id.tabHistory);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchTicketsFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("booked_tickets");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTicketsList.clear();
                upcomingList.clear();
                historyList.clear();

                long currentTime = System.currentTimeMillis();
                // Quy ước: Vé mua trong vòng 24 giờ là "Sắp tới", cũ hơn là "Lịch sử"
                long oneDayInMillis = 24 * 60 * 60 * 1000;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Ticket ticket = data.getValue(Ticket.class);
                    if (ticket != null) {
                        allTicketsList.add(ticket);

                        if (currentTime - ticket.getTimestamp() < oneDayInMillis) {
                            upcomingList.add(ticket);
                        } else {
                            historyList.add(ticket);
                        }
                    }
                }

                // Đảo ngược danh sách để vé mới nhất hiện lên đầu
                Collections.reverse(upcomingList);
                Collections.reverse(historyList);

                // Mặc định hiển thị tab Sắp tới
                updateRecyclerView(upcomingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyTicketsActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView(List<Ticket> list) {
        View layoutEmpty = findViewById(R.id.layoutEmptyTickets);

        if (list == null || list.isEmpty()) {
            // Nếu không có vé: Hiện màn hình trống, ẩn danh sách
            rvTickets.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            // Nếu có vé: Hiện danh sách, ẩn màn hình trống
            rvTickets.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

            ticketAdapter = new TicketAdapter(this, list);
            rvTickets.setAdapter(ticketAdapter);
        }
    }

    private void setupTabsLogic() {
        tabUpcoming.setOnClickListener(v -> {
            // Đổi màu Tab
            tabUpcoming.setBackgroundResource(R.drawable.bg_neon_button);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.bg_main));
            tabHistory.setBackgroundResource(0);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Hiện danh sách sắp tới
            updateRecyclerView(upcomingList);
        });

        tabHistory.setOnClickListener(v -> {
            // Đổi màu Tab
            tabHistory.setBackgroundResource(R.drawable.bg_neon_button);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.bg_main));
            tabUpcoming.setBackgroundResource(0);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Hiện danh sách lịch sử
            updateRecyclerView(historyList);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tickets);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(this, MoviesActivity.class));
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(this, AiChatActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                }
                return itemId == R.id.nav_tickets;
            });
        }
    }
}