package com.example.shortcut_link.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shortcut_link.entity.Stats;

public interface StatsRepository extends JpaRepository<Stats,Integer>{
    
}
