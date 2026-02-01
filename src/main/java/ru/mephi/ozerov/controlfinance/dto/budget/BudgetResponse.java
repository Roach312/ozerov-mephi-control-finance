package ru.mephi.ozerov.controlfinance.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for budget response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
}
