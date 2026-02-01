package ru.mephi.ozerov.controlfinance.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for budget status response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetStatusResponse {

    private Long categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private boolean limitExceeded;
}
