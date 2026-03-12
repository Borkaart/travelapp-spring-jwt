package com.travelapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.dto.LoginRequest;
import com.travelapp.dto.UserCreateRequest;
import com.travelapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Remove setUp or make it empty
    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldRegisterAndLoginUser() throws Exception {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";

        // 1. Register User
        UserCreateRequest registerRequest = new UserCreateRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(uniqueEmail));

        // 2. Login User
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() throws Exception {
        String uniqueEmail = "duplicate" + System.currentTimeMillis() + "@example.com";

        UserCreateRequest registerRequest = new UserCreateRequest();
        registerRequest.setName("First User");
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword("password123");

        // First registration
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Second registration with same email
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict()); // Assuming 409 Conflict
    }
}
