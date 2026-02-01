package ru.mephi.ozerov.controlfinance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;
import ru.mephi.ozerov.controlfinance.service.impl.ExportServiceImpl;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock private StatisticsService statisticsService;

    @Mock private TransactionService transactionService;

    @InjectMocks private ExportServiceImpl exportService;

    @Test
    void exportSummaryAsText_ShouldReturnFormattedText() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("2000.00"))
                        .totalExpense(new BigDecimal("1000.00"))
                        .balance(new BigDecimal("1000.00"))
                        .build();

        CategorySummaryResponse categorySummary =
                CategorySummaryResponse.builder()
                        .categoryId(1L)
                        .categoryName("Food")
                        .categoryType(CategoryType.EXPENSE)
                        .totalAmount(new BigDecimal("500.00"))
                        .build();

        BudgetStatusResponse budgetStatus =
                BudgetStatusResponse.builder()
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("600.00"))
                        .spentAmount(new BigDecimal("500.00"))
                        .remainingAmount(new BigDecimal("100.00"))
                        .limitExceeded(false)
                        .build();

        TransactionResponse transaction =
                TransactionResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("100.00"))
                        .type(TransactionType.EXPENSE)
                        .categoryId(1L)
                        .categoryName("Food")
                        .description("Lunch")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(statisticsService.getSummary()).thenReturn(summary);
        when(statisticsService.getSummaryByCategories(isNull()))
                .thenReturn(List.of(categorySummary));
        when(statisticsService.getBudgetStatus()).thenReturn(List.of(budgetStatus));
        when(transactionService.getAllTransactions()).thenReturn(List.of(transaction));

        String result = exportService.exportSummaryAsText();

        assertNotNull(result);
        assertTrue(result.contains("FINANCIAL SUMMARY"));
        assertTrue(result.contains("Total Income: 2000.00"));
        assertTrue(result.contains("Total Expense: 1000.00"));
        assertTrue(result.contains("Food"));
        assertTrue(result.contains("CATEGORIES"));
        assertTrue(result.contains("BUDGET STATUS"));
        assertTrue(result.contains("RECENT TRANSACTIONS"));
    }

    @Test
    void exportSummaryAsJson_ShouldReturnValidJson() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("2000.00"))
                        .totalExpense(new BigDecimal("1000.00"))
                        .balance(new BigDecimal("1000.00"))
                        .build();

        when(statisticsService.getSummary()).thenReturn(summary);
        when(statisticsService.getSummaryByCategories(isNull())).thenReturn(List.of());
        when(statisticsService.getBudgetStatus()).thenReturn(List.of());
        when(transactionService.getAllTransactions()).thenReturn(List.of());

        String result = exportService.exportSummaryAsJson();

        assertNotNull(result);
        assertTrue(result.contains("summary"));
        assertTrue(result.contains("totalIncome"));
        assertTrue(result.contains("2000.00"));
    }
}
