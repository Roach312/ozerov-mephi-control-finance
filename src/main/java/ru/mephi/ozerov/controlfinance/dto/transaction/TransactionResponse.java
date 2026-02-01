package ru.mephi.ozerov.controlfinance.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;

/** DTO for transaction response. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private Long categoryId;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;
    private Long transferId;
}
