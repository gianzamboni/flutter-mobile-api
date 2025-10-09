package com.pines.flutter.capacitacion.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pines.flutter.capacitacion.api.model.user.User;
import com.pines.flutter.capacitacion.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register a new user with HTTP 201 and return user data")
    void registerUser_ShouldCreateUserAndReturn201() throws Exception {
        Map<String, Object> registration = new HashMap<>();
        registration.put("name", "John");
        registration.put("surname", "Doe");
        registration.put("username", "jdoe");
        registration.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username").value("jdoe"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    @DisplayName("Should reject duplicate username with HTTP 400")
    void registerUser_DuplicateUsername_ShouldReturn400() throws Exception {
        User existing = new User();
        existing.setName("Jane");
        existing.setSurname("Doe");
        existing.setUsername("janedoe");
        existing.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(existing);

        Map<String, Object> registration = new HashMap<>();
        registration.put("name", "Jane");
        registration.put("surname", "Doe");
        registration.put("username", "janedoe");
        registration.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username already exists")));
    }


    @Test
    @DisplayName("Should reject invalid body with HTTP 400")
    void registerUser_InvalidBody_ShouldReturn400() throws Exception {
        Map<String, Object> registration = new HashMap<>();
        registration.put("name", "John");
        registration.put("surname", "Doe");
        registration.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username is required")));   
    }

    @Test
    @DisplayName("Should login successfully and return JWT with HTTP 200")
    void loginUser_ShouldReturnTokenAndUserData() throws Exception {
        User user = new User();
        user.setName("Alice");
        user.setSurname("Smith");
        user.setUsername("asmith");
        user.setPassword(passwordEncoder.encode("password123"));
        user = userRepository.save(user);

        Map<String, Object> login = new HashMap<>();
        login.put("username", "asmith");
        login.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", allOf(notNullValue(), not(emptyString()))))
                .andExpect(jsonPath("$.userId", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username").value("asmith"))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.surname").value("Smith"));
    }

    @Test
    @DisplayName("Should reject invalid credentials with HTTP 401")
    void loginUser_InvalidCredentials_ShouldReturn401() throws Exception {
        User user = new User();
        user.setName("Bob");
        user.setSurname("Brown");
        user.setUsername("bbrown");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        Map<String, Object> login = new HashMap<>();
        login.put("username", "bbrown");
        login.put("password", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid username or password")));
    }
}


