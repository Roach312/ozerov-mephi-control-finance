package ru.mephi.ozerov.controlfinance.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mephi.ozerov.controlfinance.dto.auth.LoginRequest;
import ru.mephi.ozerov.controlfinance.dto.auth.RegisterRequest;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    private String uniqueLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void register_ShouldCreateUserSuccessfully() throws Exception {
        String login = uniqueLogin();
        RegisterRequest request =
                RegisterRequest.builder().login(login).password("password123").build();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.login").value(login));
    }

    @Test
    void register_ShouldReturnConflictWhenUserExists() throws Exception {
        String login = uniqueLogin();
        RegisterRequest request =
                RegisterRequest.builder().login(login).password("password123").build();

        // First registration
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same login
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_ShouldReturnBadRequestForInvalidInput() throws Exception {
        RegisterRequest request =
                RegisterRequest.builder()
                        .login("ab") // Too short
                        .password("123") // Too short
                        .build();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void login_ShouldLoginSuccessfully() throws Exception {
        String login = uniqueLogin();
        // First register
        RegisterRequest registerRequest =
                RegisterRequest.builder().login(login).password("password123").build();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login
        LoginRequest loginRequest =
                LoginRequest.builder().login(login).password("password123").build();

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.login").value(login));
    }

    @Test
    void login_ShouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        String login = uniqueLogin();
        // First register
        RegisterRequest registerRequest =
                RegisterRequest.builder().login(login).password("password123").build();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login with wrong password
        LoginRequest loginRequest =
                LoginRequest.builder().login(login).password("wrongpassword").build();

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
