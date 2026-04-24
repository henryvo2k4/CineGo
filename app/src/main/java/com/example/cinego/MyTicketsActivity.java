package com.example.cinego;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvTickets;
    private TicketAdapter ticketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        // 1. Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvMyTickets);

        // 2. Xử lý nút quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Đổ dữ liệu vào danh sách
        if (rvTickets != null) {
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        // Tạo dữ liệu giả lập (Dummy Data) các vé đã mua
        List<Ticket> list = new ArrayList<>();
        list.add(new Ticket("Avatar: Dòng Chảy Của Nước", "CineGo Landmark 81", "14 Th 10 • 13:15", "M1, M2", R.drawable.img_bg_login));
        list.add(new Ticket("Lật Mặt 6: Tấm Vé Định Mệnh", "CineGo Giga Mall", "20 Th 10 • 19:00", "G4, G5", R.drawable.img_bg_login));
        list.add(new Ticket("Doraemon: Khủng Long Nobita", "CineGo Sư Vạn Hạnh", "25 Th 10 • 09:30", "A1, A2, A3", R.drawable.img_bg_login));

        // Gắn Adapter vào RecyclerView
        ticketAdapter = new TicketAdapter(this, list);

        // Dùng LinearLayoutManager (chiều dọc mặc định) vì danh sách vé cuộn dọc
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setAdapter(ticketAdapter);
    }
}
