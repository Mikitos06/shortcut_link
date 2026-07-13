package com.example.shortcut_link.service;

import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.entity.Stats;
import com.example.shortcut_link.repository.StatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    public void createStatsForLink(Link link) {
        Stats stats = new Stats(link);
        statsRepository.save(stats);
    }

    @Transactional(readOnly = true)
    public Long getClickCount(Link link) {
        return statsRepository.findByLinkId(link.getId())
                .map(Stats::getClickCount)
                .orElse(0L);
    }
}
