package com.example.shortcut_link.repository;

import com.example.shortcut_link.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StatsRepository extends JpaRepository<Stats, Integer> {

    Optional<Stats> findByLinkId(Integer linkId);

    @Modifying
    @Transactional
    @Query("UPDATE Stats s SET s.clickCount = s.clickCount + 1 WHERE s.link.id = :linkId")
    void incrementClickCount(@Param("linkId") Integer linkId);

    void deleteByLinkId(Integer linkId);
}
