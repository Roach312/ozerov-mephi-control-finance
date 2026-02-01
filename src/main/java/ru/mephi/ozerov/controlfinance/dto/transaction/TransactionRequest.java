package ru.mephi.ozerov.controlfinance.dto.transaction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;

/** DTO for transaction creation request. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") private BigDecimal amount;

    @NotNull(message = "Transaction type is required") private TransactionType type;

    @NotNull(message = "Category ID is required") private Long categoryId;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}
