package com.example.shortcut_link.service;


import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.entity.User;
import com.example.shortcut_link.exception.NotFoundException;
import com.example.shortcut_link.exception.ShortCodeAlreadyExistsException;
import com.example.shortcut_link.repository.LinkRepository;
import com.example.shortcut_link.repository.StatsRepository;
import com.example.shortcut_link.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class LinkService {

    private final StatsService statsService;
    private final LinkRepository linkRepository;
    private final StatsRepository statsRepository;
    private final UserRepository userRepository;

    public LinkService(LinkRepository linkRepository, StatsRepository statsRepository, StatsService statsService,UserRepository userRepository){
        this.linkRepository = linkRepository;
        this.statsRepository = statsRepository;
        this.statsService = statsService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Link createLink(String originalURL, String shortCode, LocalDateTime expiresAt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
        if (linkRepository.findByShortCode(shortCode).isPresent()) {
        throw new ShortCodeAlreadyExistsException("Short code already exists: ");
        }

        Link link = new Link();
        link.setOriginalURL(originalURL);
        link.setShortCode(shortCode);
        link.setExpiresAt(expiresAt);
        link.setUser(user);
        Link savedLink = linkRepository.save(link);
        statsService.createStatsForLink(savedLink);
        return savedLink;
    }

    @Transactional
    public Link createLink(String originalURL,LocalDateTime expiresAt) {
        Link link = new Link();
        link.setShortCode(generateShortCode());
        link.setOriginalURL(originalURL);
        link.setExpiresAt(expiresAt);
        Link savedLink = linkRepository.save(link);
        statsService.createStatsForLink(savedLink);
        return savedLink;
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
        statsRepository.incrementClickCount(link.getId());
        return link.getOriginalURL();
    }

    @Transactional
    public void toggleLinkActivation(String shortCode, boolean active) {
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new NotFoundException("Link with this short code was not found."));
        link.setIsActive(active);
    }

    @Transactional
    public void deleteLink(String shortCode) {
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new NotFoundException("Link with this short code was not found."));
        
        statsRepository.deleteByLinkId(link.getId());
        
        linkRepository.delete(link);
    }

    public Link findLinkByShortCode(String shortCode){
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new NotFoundException("Link with this short code was not found."));
        return link;
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
