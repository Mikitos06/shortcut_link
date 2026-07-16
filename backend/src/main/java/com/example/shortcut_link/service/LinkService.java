package com.example.shortcut_link.service;

import org.springframework.security.access.AccessDeniedException;
import com.example.shortcut_link.DTO.LinkListResponse;
import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.entity.Stats;
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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        .orElseThrow(() -> new NotFoundException("User not found"));
        if (linkRepository.findByShortCode(shortCode).isPresent()) {
        throw new ShortCodeAlreadyExistsException("Short code already exists: ");
        }

        Link link = new Link();
        link.setOriginalURL(originalURL);
        link.setShortCode(shortCode);
        link.setExpiresAt(expiresAt);
        link.setIsActive(true);
        link.setCreatedAt(LocalDateTime.now());
        link.setUser(user);
        Link savedLink = linkRepository.save(link);
        statsService.createStatsForLink(savedLink);
        return savedLink;
    }

    @Transactional
    public Link createLink(String originalURL,LocalDateTime expiresAt) {
        String shortCode = generateShortCode();
        while (linkRepository.findByShortCode(shortCode).isPresent()) {
            shortCode = generateShortCode();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("User not found"));

        Link link = new Link();
        link.setOriginalURL(originalURL);
        link.setShortCode(shortCode);
        link.setExpiresAt(expiresAt);
        link.setIsActive(true);
        link.setCreatedAt(LocalDateTime.now());
        link.setUser(user);
        Link savedLink = linkRepository.save(link);
        statsService.createStatsForLink(savedLink);
        return savedLink;
    }

    @Transactional()
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
        Link link = findLinkByShortCodeAndValidateOwner(shortCode);
        link.setIsActive(active);
    }

    @Transactional
    public void deleteLink(String shortCode) {
        Link link = findLinkByShortCodeAndValidateOwner(shortCode);
        statsRepository.deleteByLinkId(link.getId());
        linkRepository.delete(link);
    }

    public Link findLinkByShortCodeAndValidateOwner(String shortCode) {
    Link link = linkRepository.findByShortCode(shortCode)
    .orElseThrow(() -> new NotFoundException("Link with this short code was not found."));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("User not authenticated");
    }
    String currentUsername = auth.getName();
    if (link.getUser() == null || !link.getUser().getUsername().equals(currentUsername)) {
        throw new AccessDeniedException("You do not have permission to modify this link"); 
    }
    return link;
    }

   @Transactional(readOnly = true)
    public List<LinkListResponse> getLinksForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

        List<Link> links = linkRepository.findByUser(user);
        return links.stream().map(link -> {
            Long clickCount = statsRepository.findByLinkId(link.getId())
                .map(Stats::getClickCount)
                .orElse(0L);
            return new LinkListResponse(
                link.getId(),
                link.getOriginalURL(),
                link.getShortCode(),
                clickCount.intValue(),
                link.IsActive(),
                link.getExpiresAt()
            );
        }).collect(Collectors.toList());
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
