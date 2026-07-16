package com.example.shortcut_link.controller;

import org.springframework.security.access.AccessDeniedException;
import com.example.shortcut_link.DTO.LinkRequest;
import com.example.shortcut_link.DTO.StatsResponse;
import com.example.shortcut_link.entity.Link;
import com.example.shortcut_link.security.JwtAuthenticationFilter;
import com.example.shortcut_link.security.JwtUtil;
import com.example.shortcut_link.security.CustomUserDetailsService;
import com.example.shortcut_link.service.LinkService;
import com.example.shortcut_link.service.StatsService;
import com.example.shortcut_link.exception.NotFoundException;
import com.example.shortcut_link.exception.ShortCodeAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({LinkController.class, RedirectController.class})
@AutoConfigureMockMvc(addFilters = false)
public class TestLinkController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private StatsService statsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private LinkRequest linkRequest;
    private Link mockLink;
    private LocalDateTime expiresAt;

    @BeforeEach
    void setup() {
        linkRequest = new LinkRequest();
        linkRequest.setOriginalURL("https://example.com");
        expiresAt = LocalDateTime.now().plusDays(30);
        linkRequest.setExpiresAt(expiresAt);

        mockLink = mock(Link.class);
        when(mockLink.getShortCode()).thenReturn("ABC123");
        when(mockLink.getOriginalURL()).thenReturn("https://example.com");
        when(mockLink.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockLink.getExpiresAt()).thenReturn(expiresAt);
        when(mockLink.IsActive()).thenReturn(true);
        when(mockLink.getId()).thenReturn(1);
    }

    @Test
    @WithMockUser
    void testCreateLink_Success() throws Exception {
        when(linkService.createLink(anyString(),isNull(), any(LocalDateTime.class)))
                .thenReturn(mockLink);

        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Link created successfully"))
                .andExpect(jsonPath("$.linkURL").value("http://localhost:8080/r/ABC123"));

        verify(linkService, times(1)).createLink(anyString(),isNull(), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser
    void testCreateLinkWithCustomCode_Success() throws Exception {
        linkRequest.setShortCode("CUSTOM");
        
        Link customMockLink = mock(Link.class);
        when(customMockLink.getShortCode()).thenReturn("CUSTOM");
        when(customMockLink.getOriginalURL()).thenReturn("https://example.com");
        when(customMockLink.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(customMockLink.getExpiresAt()).thenReturn(expiresAt);
        when(customMockLink.IsActive()).thenReturn(true);
        when(customMockLink.getId()).thenReturn(2);

        when(linkService.createLink(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(customMockLink);

        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Link created successfully"))
                .andExpect(jsonPath("$.linkURL").value("http://localhost:8080/r/CUSTOM"));

        verify(linkService, times(1)).createLink(anyString(), anyString(), any(LocalDateTime.class));
    }

    @Test
    void testRedirectToOriginalURL_Success() throws Exception {
        when(linkService.findOriginalURLByShortCode("ABC123"))
                .thenReturn("https://example.com");

        mockMvc.perform(get("/r/ABC123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));

        verify(linkService, times(1)).findOriginalURLByShortCode("ABC123");
    }

    @Test
    void testRedirectToOriginalURL_NotFound() throws Exception {
        when(linkService.findOriginalURLByShortCode("INVALID"))
                .thenThrow(new NotFoundException("Link with this short code was not found."));

        mockMvc.perform(get("/r/INVALID"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:80/inactive"));

        verify(linkService, times(1)).findOriginalURLByShortCode("INVALID");
    }

    @Test
    void testRedirectToOriginalURL_Expired() throws Exception {
        when(linkService.findOriginalURLByShortCode("EXPIRED"))
                .thenThrow(new RuntimeException("Link has expired"));

        mockMvc.perform(get("/r/EXPIRED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:80/inactive"));

        verify(linkService, times(1)).findOriginalURLByShortCode("EXPIRED");
    }

    @Test
    @WithMockUser
    void testDeleteLink_Success() throws Exception {
        doNothing().when(linkService).deleteLink("ABC123");

        mockMvc.perform(delete("/api/links/ABC123"))
                .andExpect(status().isNoContent());

        verify(linkService, times(1)).deleteLink("ABC123");
    }

    @Test
    @WithMockUser
    void testDeleteLink_NotFound() throws Exception {
        doThrow(new NotFoundException("Link with this short code was not found."))
                .when(linkService).deleteLink("INVALID");

        mockMvc.perform(delete("/api/links/INVALID"))
                .andExpect(status().isNotFound());

        verify(linkService, times(1)).deleteLink("INVALID");
    }

    @Test
    @WithMockUser
    void testToggleLinkActivation_Activate() throws Exception {
        Link activeMockLink = mock(Link.class);
        when(activeMockLink.IsActive()).thenReturn(true);
        when(activeMockLink.getShortCode()).thenReturn("ABC123");
        when(activeMockLink.getId()).thenReturn(1);
        
        doNothing().when(linkService).toggleLinkActivation("ABC123", true);
        when(linkService.findLinkByShortCodeAndValidateOwner("ABC123")).thenReturn(activeMockLink);

        mockMvc.perform(patch("/api/links/ABC123/toggle?active=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Link with short code ABC123 is active"));

        verify(linkService, times(1)).toggleLinkActivation("ABC123", true);
        verify(linkService, times(1)).findLinkByShortCodeAndValidateOwner("ABC123");
    }

    @Test
    @WithMockUser
    void testToggleLinkActivation_Deactivate() throws Exception {
        Link inactiveMockLink = mock(Link.class);
        when(inactiveMockLink.IsActive()).thenReturn(false);
        when(inactiveMockLink.getShortCode()).thenReturn("ABC123");
        when(inactiveMockLink.getId()).thenReturn(1);
        
        doNothing().when(linkService).toggleLinkActivation("ABC123", false);
        when(linkService.findLinkByShortCodeAndValidateOwner("ABC123")).thenReturn(inactiveMockLink);

        mockMvc.perform(patch("/api/links/ABC123/toggle?active=false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Link with short code ABC123 is inactive"));

        verify(linkService, times(1)).toggleLinkActivation("ABC123", false);
        verify(linkService, times(1)).findLinkByShortCodeAndValidateOwner("ABC123");
    }

    @Test
    @WithMockUser
    void testGetLinkStats_Success() throws Exception {
        StatsResponse mockResponse = new StatsResponse();
        mockResponse.setShortCode("ABC123");
        mockResponse.setOriginalURL("https://example.com");
        mockResponse.setClickCount(42L);
        mockResponse.setCreatedAt(LocalDateTime.now().minusDays(1));
        mockResponse.setExpiresAt(LocalDateTime.now().plusDays(30));
        mockResponse.setActive(true);

        when(statsService.getStats("ABC123")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/links/ABC123/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("ABC123"))
                .andExpect(jsonPath("$.originalURL").value("https://example.com"))
                .andExpect(jsonPath("$.clickCount").value(42))
                .andExpect(jsonPath("$.active").value(true));

        verify(statsService, times(1)).getStats("ABC123");
    }

    @Test
    @WithMockUser
    void testGetLinkStats_NotFound() throws Exception {
        when(statsService.getStats("INVALID"))
                .thenThrow(new NotFoundException("Link not found for code: INVALID"));

        mockMvc.perform(get("/api/links/INVALID/stats"))
                .andExpect(status().isNotFound());

        verify(statsService, times(1)).getStats("INVALID");
    }

    @Test
    @WithMockUser
    void testFirstLinkStaysActiveWhenDuplicateAttempted() throws Exception {
        linkRequest.setShortCode("CODE");

        when(linkService.createLink(anyString(), eq("CODE"), any(LocalDateTime.class)))
            .thenReturn(mockLink);

        mockMvc.perform(post("/api/links")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(linkRequest)))
            .andExpect(status().isCreated());

        when(linkService.createLink(anyString(), eq("CODE"), any(LocalDateTime.class)))
                .thenThrow(new ShortCodeAlreadyExistsException("Short code already exists"));

        mockMvc.perform(post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isConflict());

        verify(linkService, times(2)).createLink(anyString(), eq("CODE"), any(LocalDateTime.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void testDeleteLink_Forbidden_DifferentUser() throws Exception {
        doThrow(new AccessDeniedException("You don't have permission to delete this link"))
                .when(linkService).deleteLink("ABC123");

        mockMvc.perform(delete("/api/links/ABC123"))
                .andExpect(status().isForbidden());

        verify(linkService, times(1)).deleteLink("ABC123");
    }

    @Test
    @WithMockUser(username = "user1")
    void testToggleLinkActivation_Forbidden_DifferentUser() throws Exception {
        doThrow(new AccessDeniedException("You don't have permission to toggle this link"))
                .when(linkService).toggleLinkActivation("ABC123", true);

        mockMvc.perform(patch("/api/links/ABC123/toggle?active=true"))
                .andExpect(status().isForbidden());

        verify(linkService, times(1)).toggleLinkActivation("ABC123", true);
    }

    @Test
    @WithMockUser(username = "user1")
    void testToggleLinkDeactivation_Forbidden_DifferentUser() throws Exception {
        doThrow(new AccessDeniedException("You don't have permission to toggle this link"))
                .when(linkService).toggleLinkActivation("ABC123", false);

        mockMvc.perform(patch("/api/links/ABC123/toggle?active=false"))
                .andExpect(status().isForbidden());

        verify(linkService, times(1)).toggleLinkActivation("ABC123", false);
    }
}
