package com.example.shortcut_link.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.entity.User;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link,Integer>{
    Optional<Link> findByShortCode(String shortCode);
    List<Link> findByUser(User user);
}
