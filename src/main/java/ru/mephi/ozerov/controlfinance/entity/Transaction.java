package ru.mephi.ozerov.controlfinance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Сущность, представляющая финансовую транзакцию (доход или расход). Транзакции связаны с кошельком
 * и опционально с категорией и переводом.
 */
@Entity
@Table(
        name = "transactions",
        indexes = {
            @Index(name = "idx_transaction_wallet", columnList = "wallet_id"),
            @Index(name = "idx_transaction_category", columnList = "category_id"),
            @Index(name = "idx_transaction_created_at", columnList = "created_at"),
            @Index(name = "idx_transaction_type", columnList = "type"),
            @Index(name = "idx_transaction_transfer", columnList = "transfer_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
