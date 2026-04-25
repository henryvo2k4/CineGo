package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView btnBack, imgBackdrop;
    private TextView tvMovieTitle, tvSynopsis, tvRating, tvRatingCount, tvGenre, tvActors;
    private Button btnBookTicket, btnSubmitReview; // Đổi tên nút gộp
    private RatingBar ratingBar;
    private EditText edtComment;
    private LinearLayout layoutComments;

    private String movieId;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;

    // Biến để quản lý trạng thái mở rộng bình luận
    private boolean isCommentsExpanded = false;
    private List<DataSnapshot> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initViews();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance("https://cinego-7aed8-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        movieId = getIntent().getStringExtra("MOVIE_ID");
        if (movieId != null) {
            loadMovieDetails();
            loadComments();
        }

        btnBack.setOnClickListener(v -> finish());

        // FIX LỖI: CHUYỂN TRANG ĐẶT VÉ
        btnBookTicket.setOnClickListener(v -> {
            Intent bookingIntent = new Intent(MovieDetailActivity.this, BookingActivity.class);
            startActivity(bookingIntent);
        });

        // GỬI ĐÁNH GIÁ & BÌNH LUẬN (Bắt buộc cả 2 và chỉ 1 lần)
        btnSubmitReview.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "Bạn cần đăng nhập để đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            float vote = ratingBar.getRating();
            String cmtText = edtComment.getText().toString().trim();

            if (vote == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(cmtText)) {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem đã vote chưa
            dbRef.child("user_votes").child(movieId).child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            if (s.exists()) {
                                Toast.makeText(MovieDetailActivity.this, "Bạn đã đánh giá phim này rồi!", Toast.LENGTH_SHORT).show();
                            } else {
                                updateGlobalRatingAndComment(vote, cmtText);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError e) {}
                    });
        });
    }

    private void updateGlobalRatingAndComment(float newVote, String commentText) {
        dbRef.child("movies").child(movieId).runTransaction(new Transaction.Handler() {
            @NonNull @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Movie m = currentData.getValue(Movie.class);
                if (m == null) return Transaction.success(currentData);
                int newCount = m.getRatingCount() + 1;
                double newRating = ((m.getRating() * m.getRatingCount()) + newVote) / newCount;
                m.setRating(newRating);
                m.setRatingCount(newCount);
                currentData.setValue(m);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError e, boolean b, DataSnapshot s) {
                // 1. Lưu điểm vote để chặn lần sau
                dbRef.child("user_votes").child(movieId).child(currentUser.getUid()).setValue(newVote);

                // 2. Lưu bình luận có chứa số sao
                String user = currentUser != null ? currentUser.getEmail().split("@")[0] : "Guest";
                Map<String, Object> commentData = new HashMap<>();
                commentData.put("user", user);
                commentData.put("content", commentText);
                commentData.put("rating", newVote);

                dbRef.child("comments").child(movieId).push().setValue(commentData);

                Toast.makeText(MovieDetailActivity.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();

                // Xóa form sau khi gửi
                ratingBar.setRating(0);
                edtComment.setText("");
            }
        });
    }

    private void loadMovieDetails() {
        dbRef.child("movies").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                Movie m = s.getValue(Movie.class);
                if (m != null) {
                    tvMovieTitle.setText(m.getTitle());
                    tvSynopsis.setText(m.getSynopsis());
                    tvRating.setText(String.format("⭐ %.1f/10", m.getRating()));
                    tvRatingCount.setText("(" + m.getRatingCount() + " đánh giá)");
                    tvGenre.setText(m.getGenre());
                    tvActors.setText("Diễn viên: " + m.getActors());
                    Glide.with(MovieDetailActivity.this).load(m.getPosterUrl()).into(imgBackdrop);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void loadComments() {
        dbRef.child("comments").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                commentList.clear();
                for (DataSnapshot ds : s.getChildren()) {
                    commentList.add(ds);
                }
                // Đảo ngược danh sách để bình luận mới nhất hiển thị lên đầu
                Collections.reverse(commentList);
                renderComments();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void renderComments() {
        layoutComments.removeAllViews();

        int totalComments = commentList.size();
        if (totalComments == 0) return;

        // Nếu chưa mở rộng và có nhiều hơn 5 bình luận, chỉ hiển thị 3
        int displayCount = totalComments;
        if (!isCommentsExpanded && totalComments > 5) {
            displayCount = 3;
        }

        // Bơm dữ liệu vào file khuôn mẫu item_comment.xml
        for (int i = 0; i < displayCount; i++) {
            DataSnapshot ds = commentList.get(i);
            View commentView = LayoutInflater.from(this).inflate(R.layout.item_comment, layoutComments, false);

            TextView tvName = commentView.findViewById(R.id.tvCommentName);
            TextView tvContent = commentView.findViewById(R.id.tvCommentContent);
            TextView tvCommentRating = commentView.findViewById(R.id.tvCommentRating);

            tvName.setText(ds.child("user").getValue(String.class));
            tvContent.setText(ds.child("content").getValue(String.class));

            Double rating = ds.child("rating").getValue(Double.class);
            if (rating != null) {
                tvCommentRating.setText("⭐ " + rating);
            } else {
                tvCommentRating.setText(""); // Dành cho các comment cũ chưa có sao
            }

            layoutComments.addView(commentView);
        }

        // Tạo nút "Xem tất cả" nếu bị ẩn
        if (!isCommentsExpanded && totalComments > 5) {
            TextView tvViewAll = new TextView(this);
            tvViewAll.setText("Xem tất cả " + totalComments + " bình luận");
            tvViewAll.setTextColor(getResources().getColor(R.color.neon_cyan));
            tvViewAll.setTextSize(14);
            tvViewAll.setPadding(0, 24, 0, 24);
            tvViewAll.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvViewAll.setOnClickListener(v -> {
                isCommentsExpanded = true;
                renderComments(); // Gọi lại hàm để vẽ toàn bộ
            });
            layoutComments.addView(tvViewAll);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgBackdrop = findViewById(R.id.imgBackdrop);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvSynopsis = findViewById(R.id.tvSynopsis);
        tvRating = findViewById(R.id.tvRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvGenre = findViewById(R.id.tvGenre);
        tvActors = findViewById(R.id.tvActors);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        edtComment = findViewById(R.id.edtComment);
        layoutComments = findViewById(R.id.layoutComments);
        btnBookTicket = findViewById(R.id.btnBookTicket);
    }
}