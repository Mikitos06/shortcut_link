package com.example.shortcut_link.controller;

import com.example.shortcut_link.DTO.LinkRequest;
import com.example.shortcut_link.service.LinkService;
import com.example.shortcut_link.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LinkController.class)
public class TestLinkController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    private LinkRequest linkRequest;

    @BeforeEach
    void setup() {
        linkRequest = new LinkRequest();
        linkRequest.setOriginalURL("https://example.com");
        linkRequest.setExpiresAt(LocalDateTime.now().plusDays(30));
    }

    @Test
    void testCreateLink_Success() throws Exception {
        when(linkService.createLink(anyString(), any(LocalDateTime.class)))
                .thenReturn(1);

        mockMvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Link created successfully"));

        verify(linkService, times(1)).createLink(anyString(), any(LocalDateTime.class));
    }

    @Test
    void testCreateLinkWithCustomCode_Success() throws Exception {
        linkRequest.setShortCode("ABC123");

        when(linkService.createLink(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(1);

        mockMvc.perform(post("/links/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Link created successfully"));

        verify(linkService, times(1)).createLink(anyString(), anyString(), any(LocalDateTime.class));
    }

    @Test
    void testRedirectToOriginalURL_Success() throws Exception {
        when(linkService.findOriginalURLByShortCode("ABC123"))
                .thenReturn("https://example.com");

        mockMvc.perform(get("/ABC123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));

        verify(linkService, times(1)).findOriginalURLByShortCode("ABC123");
    }


    @Test
    void testRedirectToOriginalURL_NotFound() throws Exception {
        when(linkService.findOriginalURLByShortCode("INVALID"))
                .thenThrow(new NotFoundException("Link with this short code was not found."));

        mockMvc.perform(get("/INVALID"))
                .andExpect(status().isNotFound());

        verify(linkService, times(1)).findOriginalURLByShortCode("INVALID");
    }

    @Test
    void testRedirectToOriginalURL_Expired() throws Exception {
        when(linkService.findOriginalURLByShortCode("EXPIRED"))
                .thenThrow(new RuntimeException("Link has expired"));

        mockMvc.perform(get("/EXPIRED"))
                .andExpect(status().isInternalServerError());

        verify(linkService, times(1)).findOriginalURLByShortCode("EXPIRED");
    }

    @Test
    void testDeleteLink_Success() throws Exception {
        doNothing().when(linkService).deleteLink("ABC123");

        mockMvc.perform(delete("/links/ABC123"))
                .andExpect(status().isNoContent());

        verify(linkService, times(1)).deleteLink("ABC123");
    }

    @Test
    void testDeleteLink_NotFound() throws Exception {
        doThrow(new NotFoundException("Link with this short code was not found."))
                .when(linkService).deleteLink("INVALID");

        mockMvc.perform(delete("/links/INVALID"))
                .andExpect(status().isNotFound());

        verify(linkService, times(1)).deleteLink("INVALID");
    }

    @Test
    void testToggleLinkActivation_Success() throws Exception {
        doNothing().when(linkService).toggleLinkActivation("ABC123", true);

        mockMvc.perform(patch("/links/ABC123/toggle?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Link activation toggled"));

        verify(linkService, times(1)).toggleLinkActivation("ABC123", true);
    }

    @Test
    void testToggleLinkActivation_Deactivate() throws Exception {
        doNothing().when(linkService).toggleLinkActivation("ABC123", false);

        mockMvc.perform(patch("/links/ABC123/toggle?active=false"))
                .andExpect(status().isOk());

        verify(linkService, times(1)).toggleLinkActivation("ABC123", false);
    }
}
