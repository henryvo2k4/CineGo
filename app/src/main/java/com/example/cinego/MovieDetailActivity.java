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
    private Button btnBookTicket, btnSubmitReview;
    private RatingBar ratingBar;
    private EditText edtComment;
    private LinearLayout layoutComments;


    private String movieId;
    private String nameStr = ""; // Biến lưu tên phim
    private String posterStr = ""; // Biến lưu ảnh phim
    private String trailerUrlStr = "";
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;

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
        // Hứng dữ liệu từ Adapter gửi sang
        nameStr = getIntent().getStringExtra("movieName");
        posterStr = getIntent().getStringExtra("posterUrl");

        if (movieId != null) {
            loadMovieDetails();
            loadComments();
        }
        // xử lý nút trailer
        findViewById(R.id.btnWatchTrailer).setOnClickListener(v -> {
            if (trailerUrlStr != null && !trailerUrlStr.isEmpty()) {
                // Lệnh mở ứng dụng YouTube hoặc Trình duyệt web
                Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(trailerUrlStr));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Phim này hiện chưa có Trailer!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        // --- FIX LỖI QUAN TRỌNG TẠI ĐÂY ---
        btnBookTicket.setOnClickListener(v -> {
            Intent bookingIntent = new Intent(MovieDetailActivity.this, BookingActivity.class);

            // Truyền tiếp sức dữ liệu sang BookingActivity
            bookingIntent.putExtra("movieName", nameStr);
            bookingIntent.putExtra("posterUrl", posterStr);

            startActivity(bookingIntent);
        });

        btnSubmitReview.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "Bạn cần đăng nhập để đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }
            float vote = ratingBar.getRating();
            String cmtText = edtComment.getText().toString().trim();
            if (vote == 0 || TextUtils.isEmpty(cmtText)) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }
            dbRef.child("user_votes").child(movieId).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot s) {
                    if (s.exists())
                        Toast.makeText(MovieDetailActivity.this, "Bạn đã đánh giá rồi!", Toast.LENGTH_SHORT).show();
                    else updateGlobalRatingAndComment(vote, cmtText);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError e) {
                }
            });
        });
    }

    private void loadMovieDetails() {
        dbRef.child("movies").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                Movie m = s.getValue(Movie.class);
                if (m != null) {
                    // Cập nhật lại biến nếu dữ liệu Firebase mới hơn
                    nameStr = m.getTitle();
                    posterStr = m.getPosterUrl();

                    trailerUrlStr = m.getTrailerUrl();//trailer

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
            public void onCancelled(@NonNull DatabaseError e) {
            }
        });
    }

    private void updateGlobalRatingAndComment(float newVote, String commentText) {
        dbRef.child("movies").child(movieId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
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
                dbRef.child("user_votes").child(movieId).child(currentUser.getUid()).setValue(newVote);
                String user = currentUser.getEmail().split("@")[0];
                Map<String, Object> commentData = new HashMap<>();
                commentData.put("user", user);
                commentData.put("content", commentText);
                commentData.put("rating", newVote);
                dbRef.child("comments").child(movieId).push().setValue(commentData);
                Toast.makeText(MovieDetailActivity.this, "Cảm ơn bạn!", Toast.LENGTH_SHORT).show();
                ratingBar.setRating(0);
                edtComment.setText("");
            }
        });
    }

    private void loadComments() {
        dbRef.child("comments").child(movieId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                commentList.clear();
                for (DataSnapshot ds : s.getChildren()) commentList.add(ds);
                Collections.reverse(commentList);
                renderComments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
            }
        });
    }

    private void renderComments() {
        layoutComments.removeAllViews();
        int displayCount = isCommentsExpanded ? commentList.size() : Math.min(commentList.size(), 3);
        for (int i = 0; i < displayCount; i++) {
            DataSnapshot ds = commentList.get(i);
            View v = LayoutInflater.from(this).inflate(R.layout.item_comment, layoutComments, false);
            ((TextView) v.findViewById(R.id.tvCommentName)).setText(ds.child("user").getValue(String.class));
            ((TextView) v.findViewById(R.id.tvCommentContent)).setText(ds.child("content").getValue(String.class));
            Double r = ds.child("rating").getValue(Double.class);
            if (r != null) ((TextView) v.findViewById(R.id.tvCommentRating)).setText("⭐ " + r);
            layoutComments.addView(v);
        }
        if (!isCommentsExpanded && commentList.size() > 3) {
            TextView tv = new TextView(this);
            tv.setText("Xem tất cả bình luận");
            tv.setTextColor(getResources().getColor(R.color.neon_cyan));
            tv.setPadding(0, 20, 0, 20);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tv.setOnClickListener(v -> {
                isCommentsExpanded = true;
                renderComments();
            });
            layoutComments.addView(tv);
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