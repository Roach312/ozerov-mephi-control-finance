package ru.mephi.ozerov.controlfinance.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for financial summary response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
}
