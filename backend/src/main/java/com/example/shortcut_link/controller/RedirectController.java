package com.example.shortcut_link.controller;

import com.example.shortcut_link.service.LinkService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/r")
public class RedirectController {

    private final LinkService linkService;

    @Value("${FRONTEND_URL}")
    private String frontUrl;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalURL(@PathVariable String shortCode) {
        try {
            String originalURL = linkService.findOriginalURLByShortCode(shortCode);
            return new RedirectView(originalURL);
        } catch (Exception e) {
             return new RedirectView(frontUrl+"/inactive");
        }
    }
}
