package com.example.shortcut_link.service;

import com.example.shortcut_link.DTO.StatsResponse;
import com.example.shortcut_link.exception.NotFoundException;
import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.entity.Stats;
import com.example.shortcut_link.repository.LinkRepository;
import com.example.shortcut_link.repository.StatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StatsService {

    private final StatsRepository statsRepository;
    private final LinkRepository linkRepository;

    public StatsService(StatsRepository statsRepository, LinkRepository linkRepository) {
        this.statsRepository = statsRepository;
        this.linkRepository = linkRepository;
    }

    @Transactional
    public void createStatsForLink(Link link) {
        Stats stats = new Stats(link);
        statsRepository.save(stats);
    }

    @Transactional(readOnly = true)
    public StatsResponse getStats(String shortCode) {
        if (shortCode == null) {
        throw new IllegalArgumentException("shortCode must not be null");
        }
        Link link = linkRepository.findByShortCode(shortCode)
        .orElseThrow(() -> new NotFoundException("Link with shortCode " + shortCode + " not found"));
        Stats stats = statsRepository.findByLinkId(link.getId())
        .orElseThrow(() -> new NotFoundException("Stats not found for link with id: " + link.getId()));
        return new StatsResponse(link,stats.getClickCount());
    }
}
