package com.example.shortcut_link.DTO;

import java.time.LocalDateTime;

public class LinkListResponse {
    private Long id;
    private String originalUrl;
    private String shortCode;
    private Integer clickCount;
    private Boolean isActive;
    private LocalDateTime expiresAt;

    public LinkListResponse() {}

    public LinkListResponse(Long id, String originalUrl, String shortCode, Integer clickCount, Boolean isActive, LocalDateTime expiresAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = clickCount;
        this.isActive = isActive;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
