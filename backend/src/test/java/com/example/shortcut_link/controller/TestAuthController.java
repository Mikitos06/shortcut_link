package com.example.shortcut_link.controller;

import com.example.shortcut_link.DTO.LoginRequest;
import com.example.shortcut_link.DTO.RegisterRequest;
import com.example.shortcut_link.entity.User;
import com.example.shortcut_link.exception.UserAlreadyExistsException;
import com.example.shortcut_link.exception.UserUnauthorizedException;
import com.example.shortcut_link.security.JwtAuthenticationFilter;
import com.example.shortcut_link.security.JwtUtil;
import com.example.shortcut_link.service.AuthService;
import com.example.shortcut_link.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getId()).thenReturn(1);
    }

    @Test
    void testRegister_Success() throws Exception {
        when(authService.register("testuser", "password123", "test@example.com"))
                .thenReturn(mockUser);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(authService, times(1)).register("testuser", "password123", "test@example.com");
    }

    @Test
    void testRegister_UsernameAlreadyExists() throws Exception {
        when(authService.register(anyString(), anyString(), anyString()))
                .thenThrow(new UserAlreadyExistsException("Username already taken"));

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(authService, times(1)).register(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        when(authService.register(anyString(), anyString(), anyString()))
                .thenThrow(new UserAlreadyExistsException("Email already registered"));

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(authService, times(1)).register(anyString(), anyString(), anyString());
    }

    @Test
    void testLogin_Success() throws Exception {
        when(authService.authenticate("testuser", "password123"))
                .thenReturn(mockUser);
        when(jwtUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(authService, times(1)).authenticate("testuser", "password123");
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
    when(authService.authenticate(anyString(), anyString()))
            .thenThrow(new UserUnauthorizedException("Invalid username or password"));

    mockMvc.perform(post("/api/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized());

    verify(authService, times(1)).authenticate(anyString(), anyString());
    }

    @Test
    void testLogin_MissingFields() throws Exception {
        LoginRequest emptyRequest = new LoginRequest();
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }
}
