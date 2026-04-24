package com.example.cinego;

public class Ticket {
    private String movieName;
    private String cinemaName;
    private String time;
    private String seats;
    private int posterResId;

    public Ticket(String movieName, String cinemaName, String time, String seats, int posterResId) {
        this.movieName = movieName;
        this.cinemaName = cinemaName;
        this.time = time;
        this.seats = seats;
        this.posterResId = posterResId;
    }

    public String getMovieName() { return movieName; }
    public String getCinemaName() { return cinemaName; }
    public String getTime() { return time; }
    public String getSeats() { return seats; }
    public int getPosterResId() { return posterResId; }
}