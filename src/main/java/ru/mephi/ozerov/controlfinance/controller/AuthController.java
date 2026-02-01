package ru.mephi.ozerov.controlfinance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.auth.AuthResponse;
import ru.mephi.ozerov.controlfinance.dto.auth.LoginRequest;
import ru.mephi.ozerov.controlfinance.dto.auth.RegisterRequest;
import ru.mephi.ozerov.controlfinance.service.UserService;

/**
 * Контроллер для операций аутентификации.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Регистрация нового пользователя.
     * @param request данные регистрации
     * @return ответ аутентификации
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Аутентификация пользователя.
     * @param request данные для входа
     * @return ответ аутентификации
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
