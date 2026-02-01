package ru.mephi.ozerov.controlfinance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mephi.ozerov.controlfinance.dto.auth.AuthResponse;
import ru.mephi.ozerov.controlfinance.dto.auth.LoginRequest;
import ru.mephi.ozerov.controlfinance.dto.auth.RegisterRequest;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.exception.EntityAlreadyExistsException;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.UserRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .login("testuser")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .login("testuser")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("hashedPassword")
                .build();
    }

    @Test
    void register_ShouldCreateUserAndWallet() {
        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(walletRepository.save(any())).thenReturn(null);

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertEquals("testuser", response.getLogin());
        verify(userRepository).save(any(User.class));
        verify(walletRepository).save(any());
    }

    @Test
    void register_ShouldThrowExceptionWhenUserExists() {
        when(userRepository.existsByLogin("testuser")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        AuthResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("testuser", response.getLogin());
    }

    @Test
    void login_ShouldThrowExceptionOnBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.login(loginRequest));
    }

    @Test
    void getUserByLogin_ShouldReturnUser() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        User result = userService.getUserByLogin("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
    }

    @Test
    void getUserByLogin_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByLogin("nonexistent"));
    }
}
