package com.example.cinego;

public class Ticket {
    private String id;
    private String movieName;
    private String cinemaName;
    private String dateTime;
    private String seats;
    private String snacks;
    private String totalPrice;
    private String posterUrl;
    private long timestamp;

    // Bắt buộc phải có Constructor trống cho Firebase
    public Ticket() {}

    public Ticket(String id, String movieName, String cinemaName, String dateTime, String seats, String snacks, String totalPrice, String posterUrl, long timestamp) {
        this.id = id;
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.dateTime = dateTime;
        this.seats = seats;
        this.snacks = snacks;
        this.totalPrice = totalPrice;
        this.posterUrl = posterUrl;
        this.timestamp = timestamp;
    }

    // Getter và Setter (Bạn có thể nhấn Alt + Insert để tự tạo nhanh)
    public String getId() { return id; }
    public String getMovieName() { return movieName; }
    public String getCinemaName() { return cinemaName; }
    public String getDateTime() { return dateTime; }
    public String getSeats() { return seats; }
    public String getSnacks() { return snacks; }
    public String getTotalPrice() { return totalPrice; }
    public String getPosterUrl() { return posterUrl; }
    public long getTimestamp() { return timestamp; }
}