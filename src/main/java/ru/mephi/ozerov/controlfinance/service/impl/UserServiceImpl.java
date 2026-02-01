package ru.mephi.ozerov.controlfinance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.auth.AuthResponse;
import ru.mephi.ozerov.controlfinance.dto.auth.LoginRequest;
import ru.mephi.ozerov.controlfinance.dto.auth.RegisterRequest;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.EntityAlreadyExistsException;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.UserRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.UserService;

import java.math.BigDecimal;

/**
 * Реализация сервиса пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new EntityAlreadyExistsException("User with login '" + request.getLogin() + "' already exists");
        }

        User user = User.builder()
                .login(request.getLogin())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        // Создаём кошелёк для пользователя
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);

        return AuthResponse.builder()
                .message("User registered successfully")
                .login(user.getLogin())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthResponse.builder()
                .message("Login successful")
                .login(request.getLogin())
                .build();
    }

    @Override
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + login));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new EntityNotFoundException("No authenticated user found");
        }
        String login = authentication.getName();
        return getUserByLogin(login);
    }
}
