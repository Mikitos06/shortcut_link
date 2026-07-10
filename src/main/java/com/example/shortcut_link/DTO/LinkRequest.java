package com.example.shortcut_link.DTO;

import java.time.LocalDateTime;

public class LinkRequest {
    private String originalURL;
    private String shortCode;
    private LocalDateTime expiresAt;

    public LinkRequest() {}

    public LinkRequest(String originalURL, String shortCode, LocalDateTime expiresAt) {
        this.originalURL = originalURL;
        this.shortCode = shortCode;
        this.expiresAt = expiresAt;
    }

    public String getOriginalURL() { return originalURL; }
    public void setOriginalURL(String originalURL) { this.originalURL = originalURL; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
