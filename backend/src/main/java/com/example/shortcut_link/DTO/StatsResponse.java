package com.example.shortcut_link.DTO;

import java.time.LocalDateTime;

import com.example.shortcut_link.entity.Link;

public class StatsResponse {
    private String shortCode;
    private String originalURL;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;

    public StatsResponse() {}

    public StatsResponse(Link link, Long clickCount) {
        this.shortCode = link.getShortCode();
        this.originalURL = link.getOriginalURL();
        this.clickCount = clickCount;
        this.createdAt = link.getCreatedAt();
        this.expiresAt = link.getExpiresAt();
        this.isActive = link.IsActive();
    }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getOriginalURL() { return originalURL; }
    public void setOriginalURL(String originalURL) { this.originalURL = originalURL; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

}
