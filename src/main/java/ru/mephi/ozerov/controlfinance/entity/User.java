package ru.mephi.ozerov.controlfinance.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Сущность, представляющая пользователя в системе. Каждый пользователь имеет уникальный логин и
 * может иметь один кошелёк, несколько категорий и транзакций.
 */
@Entity
@Table(
        name = "users",
        indexes = {@Index(name = "idx_user_login", columnList = "login")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Wallet wallet;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();
}
