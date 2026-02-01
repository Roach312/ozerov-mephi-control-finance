package ru.mephi.ozerov.controlfinance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Сущность, представляющая лимит бюджета для конкретной категории расходов.
 * Каждый бюджет привязан к кошельку и категории с уникальным ограничением для предотвращения дублирования.
 */
@Entity
@Table(name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_budget_wallet_category", 
                        columnNames = {"wallet_id", "category_id"})
        },
        indexes = {
                @Index(name = "idx_budget_wallet", columnList = "wallet_id"),
                @Index(name = "idx_budget_category", columnList = "category_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;
}
