package com.example.shortcut_link.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import com.example.shortcut_link.service.LinkService;

@RestController
public class RedirectController {

    private final LinkService linkService;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/r/{shortCode}")
    public RedirectView redirectToOriginalURL(@PathVariable String shortCode) {
        String originalURL = linkService.findOriginalURLByShortCode(shortCode);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalURL);
        return redirectView;
    }
}
