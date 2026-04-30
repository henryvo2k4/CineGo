package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText edtMessage;
    private ImageView btnSend;

    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;

    private static final String TAG = "GROQ_DEBUG";
    private static final String API_KEY = "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";

    private String moviePrompt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        rvChat = findViewById(R.id.rvChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvChat.setAdapter(chatAdapter);
        rvChat.setLayoutManager(new LinearLayoutManager(this));

        // Nhận prompt từ MoviesActivity
        moviePrompt = getIntent().getStringExtra("movie_prompt");

        addMessage("Xin chào! Mình là AI Ci 🤖", "bot");

        btnSend.setOnClickListener(v -> {
            String q = edtMessage.getText().toString().trim();
            if (!q.isEmpty()) {
                addMessage(q, "me");
                edtMessage.setText("");
                askAI(q);
            }
        });
        setupBottomNavigation();
        // 5. Gắn sự kiện cho các nút gợi ý (Chips)
        findViewById(R.id.chipAction).setOnClickListener(v -> sendMessage("Gợi ý phim hành động hay"));
        findViewById(R.id.chipCouple).setOnClickListener(v -> sendMessage("Phim gì lãng mạn cho cặp đôi?"));
        findViewById(R.id.chipComedy).setOnClickListener(v -> sendMessage("Tìm phim hài hước nhất"));

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void sendMessage(String s) {
        addMessage(s, "me");
        edtMessage.setText("");
        askAI(s);
    }

    private void addMessage(String text, String sender) {
        runOnUiThread(() -> {
            messageList.add(new ChatMessage(text, sender));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            rvChat.scrollToPosition(messageList.size() - 1);
        });
    }

    private void askAI(String question) {
        addMessage("AI đang suy nghĩ...", "bot");
        int index = messageList.size() - 1;

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("model", "llama-3.1-8b-instant");

            JSONArray messages = new JSONArray();

            JSONObject system = new JSONObject();
            system.put("role", "system");
            String fullPrompt = (moviePrompt != null && !moviePrompt.isEmpty()) ? moviePrompt : "Bạn là trợ lý AI của CineGo.";
            system.put("content", fullPrompt + "\nLƯU Ý QUAN TRỌNG: Bạn CHỈ ĐƯỢC PHÉP giới thiệu các bộ phim có tên trong danh sách trên. Nếu người dùng hỏi về phim khác hoặc yêu cầu ngoài danh sách, hãy lịch sự trả lời rằng hiện tại ứng dụng chưa có phim đó.");
            messages.put(system);

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", question);
            messages.put(user);

            json.put("messages", messages);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        messageList.get(index).setText("Lỗi: " + e.getMessage());
                        chatAdapter.notifyItemChanged(index);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    Log.d(TAG, res);

                    try {
                        JSONObject obj = new JSONObject(res);

                        if (obj.has("error")) {
                            String err = obj.getJSONObject("error").getString("message");
                            runOnUiThread(() -> {
                                messageList.get(index).setText("API lỗi: " + err);
                                chatAdapter.notifyItemChanged(index);
                            });
                            return;
                        }

                        String reply = obj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        runOnUiThread(() -> {
                            messageList.get(index).setText(reply);
                            chatAdapter.notifyItemChanged(index);
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            messageList.get(index).setText("Parse lỗi");
                            chatAdapter.notifyItemChanged(index);
                        });
                    }
                }
            });

        } catch (Exception e) {
            messageList.get(index).setText("JSON lỗi");
            chatAdapter.notifyItemChanged(index);
        }
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
    static class ChatMessage {
        String text, sender;

        ChatMessage(String t, String s) {
            text = t;
            sender = s;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatMessage> list;

        ChatAdapter(List<ChatMessage> l) { list = l; }

        @Override
        public int getItemCount() { return list.size(); }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int v) {
            View view = LayoutInflater.from(p.getContext())
                    .inflate(android.R.layout.simple_list_item_1, p, false);
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int i) {
            TextView tv = h.itemView.findViewById(android.R.id.text1);
            ChatMessage m = list.get(i);

            tv.setText((m.sender.equals("me") ? "Bạn: " : "AI: ") + m.text);
            tv.setGravity(m.sender.equals("me") ? Gravity.END : Gravity.START);
        }
    }
}