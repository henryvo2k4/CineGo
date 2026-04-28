package com.example.cinego;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiChatActivity extends AppCompatActivity {

    private ImageView btnBack, btnSend;
    private EditText edtMessage;
    private LinearLayout layoutChatContent;
    private NestedScrollView scrollViewChat;

    // 🔴 QUAN TRỌNG: BẠN HÃY DÁN API KEY CỦA BẠN VÀO ĐÂY NHÉ
    private static final String GEMINI_API_KEY = "API";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        // 1. Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);
        layoutChatContent = findViewById(R.id.layoutChatContent);
        scrollViewChat = findViewById(R.id.scrollViewChat);

        // 2. Nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // 3. Nút Gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                handleSendMessage(message);
            }
        });
    }

    private void handleSendMessage(String message) {
        // Xóa ô nhập liệu ngay khi bấm gửi
        edtMessage.setText("");

        // 1. Hiển thị tin nhắn của người dùng lên màn hình
        addUserBubble(message);
        scrollToBottom();

        // 2. Gọi API của Gemini để lấy câu trả lời
        callGeminiAPI(message);
    }

    private void callGeminiAPI(String userText) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

        OkHttpClient client = new OkHttpClient();

        try {
            // --- BƯỚC 1: LẤY DỮ LIỆU CÁ NHÂN HÓA CỦA APP ---
            // (Hiện tại mình giả lập dữ liệu tĩnh, sau này bạn có thể truyền biến vào đây)
            String userName = "khách hàng VIP";
            String currentMovies =
                    "- Avatar: Dòng Chảy Của Nước (Giá: 90k, Giờ chiếu: 13:15, 18:00)\n" +
                            "- Lật Mặt 6: Tấm Vé Định Mệnh (Giá: 80k, Giờ chiếu: 14:00, 20:00)\n" +
                            "- Doraemon: Khủng Long Nobita (Giá: 60k, Giờ chiếu: 09:00, 15:30)\n" +
                            "- Avengers: Endgame (Giá: 90k, Giờ chiếu: 20:00)";
            String snackCombos =
                    "- Combo VIP (1 Bắp + 2 Nước lớn): 120.000đ\n" +
                            "- Combo Couple (1 Bắp + 2 Nước vừa): 95.000đ\n" +
                            "- Bắp phô mai lớn: 65.000đ";

            // --- BƯỚC 2: BƠM NGỮ CẢNH VÀO LỆNH HỆ THỐNG (SYSTEM PROMPT) ---
            String systemPrompt =
                    "Bạn là Ai Ci, trợ lý ảo thông minh, nhiệt tình và dễ thương của rạp chiếu phim CineGo Landmark 81. " +
                            "Khách hàng đang trò chuyện với bạn tên là: " + userName + ". " +
                            "QUY TẮC TRẢ LỜI: " +
                            "1. Chỉ tư vấn dựa trên danh sách phim và combo đang có của rạp dưới đây. Nếu khách hỏi phim khác, hãy lịch sự báo là rạp chưa chiếu. " +
                            "2. Luôn xưng hô là 'Ai Ci' và gọi khách bằng tên (" + userName + "). " +
                            "3. Trả lời cực kỳ ngắn gọn, tự nhiên như người thật trò chuyện qua tin nhắn, có dùng 1-2 icon cảm xúc. Tuyệt đối không sinh ra các ký tự Markdown như ** để in đậm. \n\n" +
                            "DỮ LIỆU CỦA RẠP HÔM NAY:\n" +
                            "Kho phim:\n" + currentMovies + "\n" +
                            "Combo Bắp Nước:\n" + snackCombos + "\n\n" +
                            "CÂU HỎI CỦA KHÁCH: ";

            // --- BƯỚC 3: ĐÓNG GÓI JSON GỬI ĐI ---
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentsObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject textObject = new JSONObject();

            textObject.put("text", systemPrompt + userText);
            partsArray.put(textObject);
            contentsObject.put("parts", partsArray);
            contentsArray.put(contentsObject);
            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Thực hiện gọi mạng tới Google
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        addAiBubble("Xin lỗi " + userName + ", kết nối mạng đang không ổn định. Bạn kiểm tra lại wifi giúp Ai Ci nhé! 😢");
                        scrollToBottom();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        try {
                            // Bóc tách dữ liệu JSON trả về từ API
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONArray candidates = jsonObject.getJSONArray("candidates");
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject content = firstCandidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String aiText = parts.getJSONObject(0).getString("text");

                            // Xóa bỏ các dấu sao (**) do AI hay tự thêm vào để làm đậm chữ
                            String cleanText = aiText.replace("**", "");

                            // Đẩy câu trả lời lên màn hình
                            runOnUiThread(() -> {
                                addAiBubble(cleanText);
                                scrollToBottom();
                            });

                        } catch (JSONException e) {
                            runOnUiThread(() -> addAiBubble("Ai Ci đang gặp lỗi xử lý thông tin xíu, bạn thử lại sau nha!"));
                        }
                    } else {
                        runOnUiThread(() -> addAiBubble("Lỗi kết nối máy chủ (" + response.code() + "). Bạn xem lại API Key đã dán đúng chưa nhé!"));
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // CÁC HÀM VẼ KHUNG CHAT LÊN MÀN HÌNH
    // ==========================================

    private void addUserBubble(String message) {
        LinearLayout wrapper = new LinearLayout(this);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapperParams.setMargins(0, 0, 0, dpToPx(24));
        wrapper.setLayoutParams(wrapperParams);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.END);

        TextView tvUser = new TextView(this);
        tvUser.setText(message);
        tvUser.setBackgroundResource(R.drawable.bg_neon_button);
        tvUser.setTextColor(ContextCompat.getColor(this, R.color.bg_main));
        tvUser.setTextSize(14);
        tvUser.setLineSpacing(dpToPx(4), 1);
        tvUser.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMarginStart(dpToPx(60));
        tvUser.setLayoutParams(textParams);

        wrapper.addView(tvUser);
        layoutChatContent.addView(wrapper);
    }

    private void addAiBubble(String message) {
        LinearLayout wrapper = new LinearLayout(this);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapperParams.setMargins(0, 0, 0, dpToPx(24));
        wrapper.setLayoutParams(wrapperParams);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.BOTTOM);

        ImageView imgAvatar = new ImageView(this);
        imgAvatar.setImageResource(R.drawable.ic_ai_chat);
        imgAvatar.setColorFilter(ContextCompat.getColor(this, R.color.neon_cyan));

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(28));
        imgParams.setMarginEnd(dpToPx(12));
        imgAvatar.setLayoutParams(imgParams);

        TextView tvAi = new TextView(this);
        tvAi.setText(message);
        tvAi.setBackgroundResource(R.drawable.bg_input);
        tvAi.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvAi.setTextSize(14);
        tvAi.setLineSpacing(dpToPx(4), 1);
        tvAi.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMarginEnd(dpToPx(60));
        tvAi.setLayoutParams(textParams);

        wrapper.addView(imgAvatar);
        wrapper.addView(tvAi);
        layoutChatContent.addView(wrapper);
    }

    private void scrollToBottom() {
        scrollViewChat.post(() -> scrollViewChat.fullScroll(View.FOCUS_DOWN));
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
