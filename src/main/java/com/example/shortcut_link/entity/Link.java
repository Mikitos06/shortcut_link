package com.example.shortcut_link.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "original_url", nullable = false, length = 2048)
    private String originalURL;

    @Column (name = "short_code", nullable = false, length = 20)
    private String shortCode;

    public Integer getId() { return id; }

    public String getOriginalURL() { return originalURL; }
    public void setOriginalURL(String original_url) { this.originalURL = original_url; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
}

