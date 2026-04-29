package com.example.cinego;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText edtMessage;
    private ImageView btnSend;
    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        // 1. Ánh xạ và thiết lập giao diện
        anhXaView();
        setupRecyclerView();

        // 2. KÍCH HOẠT THANH MENU (Quan trọng để không bị đứng yên)
        setupBottomNavigation();

        // 3. Tin nhắn chào mừng
        addMessage("Xin chào! 👋 Mình là trợ lý AI Ci. Bạn muốn tìm phim gì hôm nay?", "bot");

        // 4. Sự kiện gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String question = edtMessage.getText().toString().trim();
            if (!question.isEmpty()) {
                sendMessage(question);
            }
        });

        // 5. Gắn sự kiện cho các nút gợi ý (Chips)
        findViewById(R.id.chipAction).setOnClickListener(v -> sendMessage("Gợi ý phim hành động hay"));
        findViewById(R.id.chipCouple).setOnClickListener(v -> sendMessage("Phim gì lãng mạn cho cặp đôi?"));
        findViewById(R.id.chipComedy).setOnClickListener(v -> sendMessage("Tìm phim hài hước nhất"));

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void anhXaView() {
        rvChat = findViewById(R.id.rvChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChat.setAdapter(chatAdapter);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendMessage(String text) {
        addMessage(text, "me");
        edtMessage.setText("");
        askGeminiAI(text);
    }

    private void addMessage(String text, String sender) {
        runOnUiThread(() -> {
            messageList.add(new ChatMessage(text, sender));
            chatAdapter.notifyDataSetChanged();
            rvChat.scrollToPosition(messageList.size() - 1);
        });
    }

    private void askGeminiAI(String question) {
        addMessage("Ai Ci đang suy nghĩ...", "bot");
        int loadingIndex = messageList.size() - 1;

        // THAY API KEY THẬT CỦA BẠN VÀO ĐÂY
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDiW2aHpS1oo4csyO7tgKMSO8coOn5AL8s");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(question).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    messageList.get(loadingIndex).setText(result.getText());
                    chatAdapter.notifyItemChanged(loadingIndex);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    messageList.get(loadingIndex).setText("Lỗi kết nối: " + t.getMessage());
                    chatAdapter.notifyItemChanged(loadingIndex);
                });
            }
        }, this.getMainExecutor());
    }

    // --- HÀM ĐIỀU HƯỚNG TAB ---
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_ai_chat);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(this, MoviesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_tickets) {
                    startActivity(new Intent(this, MyTicketsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }

    // --- SUPPORT CLASSES ---
    public static class ChatMessage {
        private String text, sender;

        public ChatMessage(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSender() {
            return sender;
        }
    }

    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatMessage> list;

        public ChatAdapter(List<ChatMessage> list) {
            this.list = list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new RecyclerView.ViewHolder(v) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView tv = holder.itemView.findViewById(android.R.id.text1);
            ChatMessage m = list.get(position);
            tv.setText((m.getSender().equals("me") ? "Bạn: " : "AI Ci: ") + m.getText());
            tv.setTextColor(Color.WHITE);
            tv.setGravity(m.getSender().equals("me") ? Gravity.END : Gravity.START);
        }
    }
}