package com.example.cinego;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Import thư viện Glide
import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;

    // Hàm khởi tạo (Constructor)
    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện cho từng item (Đảm bảo bạn có file tên là item_movie.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Lấy bộ phim hiện tại dựa trên vị trí (position)
        Movie movie = movieList.get(position);

        // 1. Đổ dữ liệu dạng chữ (Text)
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRating.setText(String.valueOf(movie.getRating()));
        holder.tvMovieGenre.setText(movie.getGenre());

        // 2. MA THUẬT CỦA GLIDE: Đổ dữ liệu dạng ảnh từ Link Web
        Glide.with(context)
                .load(movie.getPosterUrl()) // Lấy link URL từ Firebase
                .placeholder(R.drawable.img_bg_login) // Ảnh chờ tạm thời khi mạng lag
                .error(R.drawable.img_bg_login) // Ảnh thay thế nếu link URL bị hỏng
                .into(holder.imgPosterMovie); // Bơm vào cái khung ImageView

        // 3. Xử lý sự kiện khi người dùng bấm vào một bộ phim
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Đang mở: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            // (Trong tương lai, chúng ta sẽ viết Intent ở đây để chuyển sang trang Chi Tiết Phim)
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    // Lớp nội bộ để ánh xạ View
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPosterMovie;
        TextView tvMovieTitle;
        TextView tvMovieRating;
        TextView tvMovieGenre;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View từ file item_movie.xml
            // LƯU Ý: Bạn hãy kiểm tra lại xem các ID này (R.id...) có khớp chính xác
            // với ID bạn đặt trong file item_movie.xml của bạn chưa nhé!
            imgPosterMovie = itemView.findViewById(R.id.imgPoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = itemView.findViewById(R.id.tvRating);
            tvMovieGenre = itemView.findViewById(R.id.tvGenre);
        }
    }
}