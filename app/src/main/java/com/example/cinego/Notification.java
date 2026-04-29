package com.example.cinego;

public class Notification {
    private String id;
    private String title;
    private String content;
    private String type; // "PROMO" hoặc "SYSTEM"
    private long timestamp;
    private boolean isRead;

    public Notification() {
    } // Bắt buộc cho Firebase

    public Notification(String id, String title, String content, String type, long timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}