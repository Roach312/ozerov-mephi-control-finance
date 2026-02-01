package ru.mephi.ozerov.controlfinance.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Сущность, представляющая финансовую категорию. Категории могут быть для доходов или расходов и
 * уникальны для каждого пользователя по имени и типу.
 */
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_category_user_name_type",
                    columnNames = {"user_id", "name", "type"})
        },
        indexes = {
            @Index(name = "idx_category_user", columnList = "user_id"),
            @Index(name = "idx_category_type", columnList = "type")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<Budget> budgets = new ArrayList<>();
}
