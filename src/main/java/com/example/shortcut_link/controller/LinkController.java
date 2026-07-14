package com.example.shortcut_link.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.shortcut_link.service.LinkService;
import com.example.shortcut_link.service.StatsService;

import jakarta.validation.Valid;

import com.example.shortcut_link.DTO.LinkRequest;
import com.example.shortcut_link.DTO.LinkResponse;
import com.example.shortcut_link.DTO.StatsResponse;
import com.example.shortcut_link.entity.Link;


@RestController
@RequestMapping("/api")
public class LinkController {

    private final LinkService linkService;
    private final StatsService statsService;

    public LinkController(LinkService linkService, StatsService statsService) {
        this.linkService = linkService;
        this.statsService = statsService;
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> createLink(@Valid @RequestBody LinkRequest request) {
        Link link;
        String shortCode = request.getShortCode();
        if (shortCode!=null){
            link = linkService.createLink(
                request.getOriginalURL(),
                request.getShortCode(),
                request.getExpiresAt()
            );
        }
        else{
            link = linkService.createLink(request.getOriginalURL(), request.getExpiresAt());
        }
        String baseUrl = "http://localhost:8080/r/";
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new LinkResponse(link, "Link created successfully",baseUrl + "/" + link.getShortCode()));
    }

    @DeleteMapping("/links/{shortCode}")
    public ResponseEntity<Void> deleteLink(@PathVariable String shortCode) {
        linkService.deleteLink(shortCode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/links/{shortCode}/toggle")
    public ResponseEntity<String> toggleLinkActivation(
        @PathVariable String shortCode,
        @RequestParam boolean active) {
        linkService.toggleLinkActivation(shortCode, active);
        Link link = linkService.findLinkByShortCodeAndValidateOwner(shortCode);
        String message = "Link with short code " + shortCode + " is " + (link.IsActive() ? "active" : "inactive");
        return ResponseEntity.ok(message);
}

    @GetMapping("/links/{shortCode}/stats")
    public ResponseEntity<StatsResponse> getLinkStats(@PathVariable String shortCode) {
        StatsResponse response = statsService.getStats(shortCode);
        return ResponseEntity.ok(response);
    }
    
}
