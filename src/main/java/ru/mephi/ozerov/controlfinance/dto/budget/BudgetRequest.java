package ru.mephi.ozerov.controlfinance.dto.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for budget creation/update request. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotNull(message = "Category ID is required") private Long categoryId;

    @NotNull(message = "Limit amount is required") @Positive(message = "Limit amount must be positive") private BigDecimal limitAmount;
}
