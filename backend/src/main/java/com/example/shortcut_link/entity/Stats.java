package com.example.shortcut_link.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "link_id", nullable = false, unique = true)
    private Link link;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    public Stats() {}

    public Stats(Link link) {
        this.link = link;
        this.clickCount = 0L;
    }
    public Integer getId() { return id; }

    public Link getLink() { return link; }
    public void setLink(Link link) { this.link = link; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public void incrementClick() { this.clickCount++; }
}
