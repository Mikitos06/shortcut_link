package com.example.shortcut_link.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shortcut_link.entity.Link;

public interface LinkRepository extends JpaRepository<Link,Integer>{
    
}
