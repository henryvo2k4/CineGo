package com.example.cinego;

public class Movie {
    private String title;
    private String posterUrl; // Đổi từ int sang String
    private double rating;
    private String genre;

    // 1. Hàm khởi tạo rỗng (BẮT BUỘC cho Firebase)
    public Movie() {
    }

    // 2. Hàm khởi tạo đầy đủ
    public Movie(String title, String posterUrl, double rating, String genre) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.genre = genre;
    }

    // Constructor bổ sung để hỗ trợ Resource ID (int) từ drawable
    public Movie(String title, int posterResId, double rating, String genre) {
        this.title = title;
        this.posterUrl = "android.resource://com.example.cinego/" + posterResId;
        this.rating = rating;
        this.genre = genre;
    }

    // 3. Các hàm Getter để lấy dữ liệu
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public double getRating() { return rating; }
    public String getGenre() { return genre; }
}