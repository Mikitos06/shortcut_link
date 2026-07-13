package com.example.shortcut_link.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.example.shortcut_link.service.LinkService;
import com.example.shortcut_link.DTO.LinkRequest;
import com.example.shortcut_link.DTO.LinkResponse;

@RestController
@RequestMapping
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalURL(@PathVariable String shortCode) {
        String originalURL = linkService.findOriginalURLByShortCode(shortCode);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalURL);
        return redirectView;
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> createLink(@RequestBody LinkRequest request) {
        int linkId;
        String shortCode = request.getShortCode();
        if (shortCode!=null){
            linkId = linkService.createLink(
                request.getOriginalURL(),
                request.getShortCode(),
                request.getExpiresAt()
            );
        }
        else{
            linkId = linkService.createLink(request.getOriginalURL(), request.getExpiresAt());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LinkResponse(linkId, "Link created successfully"));
    }

    @DeleteMapping("/links/{shortCode}")
    public ResponseEntity<Void> deleteLink(@PathVariable String shortCode) {
        linkService.deleteLink(shortCode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/links/{shortCode}/toggle")
    public ResponseEntity<LinkResponse> toggleLinkActivation(
            @PathVariable String shortCode,
            @RequestParam boolean active) {
        linkService.toggleLinkActivation(shortCode, active);
        return ResponseEntity.ok(new LinkResponse(0, "Link activation toggled"));
    }
}
