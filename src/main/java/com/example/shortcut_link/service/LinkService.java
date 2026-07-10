package com.example.shortcut_link.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.shortcut_link.repository.LinkRepository;
import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class LinkService {

    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }

    @Transactional
    public int createLink(String originalURL, String shortCode, LocalDateTime expiresAt) {
        Link link = new Link();
        link.setShortCode(shortCode);
        link.setOriginalURL(originalURL);
        link.setExpiresAt(expiresAt);
        link = linkRepository.save(link);
        return link.getId();
    }

    @Transactional
    public int createLink(String originalURL,LocalDateTime expiresAt) {
        Link link = new Link();
        link.setShortCode(generateShortCode());
        link.setOriginalURL(originalURL);
        link.setExpiresAt(expiresAt);
        link = linkRepository.save(link);
        return link.getId();
    }

    @Transactional(readOnly = true)
    public String findOriginalURLByShortCode(String shortCode) {
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new NotFoundException("Link with this short code was not found."));

        if (!link.IsActive()) {
            throw new RuntimeException("Link diactivate");
        }

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link has expired");
        }

        return link.getOriginalURL();
    }

    private String generateShortCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
