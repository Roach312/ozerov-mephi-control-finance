package ru.mephi.ozerov.controlfinance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.notification.NotificationResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.service.impl.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private StatisticsService statisticsService;

    @InjectMocks private NotificationServiceImpl notificationService;

    @Test
    void getNotifications_ShouldReturnEmptyListWhenNoIssues() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("2000.00"))
                        .totalExpense(new BigDecimal("1000.00"))
                        .balance(new BigDecimal("1000.00"))
                        .build();

        BudgetStatusResponse budgetStatus =
                BudgetStatusResponse.builder()
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("500.00"))
                        .spentAmount(new BigDecimal("300.00"))
                        .remainingAmount(new BigDecimal("200.00"))
                        .limitExceeded(false)
                        .build();

        when(statisticsService.getBudgetStatus()).thenReturn(List.of(budgetStatus));
        when(statisticsService.getSummary()).thenReturn(summary);

        List<NotificationResponse> notifications = notificationService.getNotifications();

        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void getNotifications_ShouldReturnBudgetExceededNotification() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("2000.00"))
                        .totalExpense(new BigDecimal("1000.00"))
                        .balance(new BigDecimal("1000.00"))
                        .build();

        BudgetStatusResponse budgetStatus =
                BudgetStatusResponse.builder()
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("500.00"))
                        .spentAmount(new BigDecimal("600.00"))
                        .remainingAmount(new BigDecimal("-100.00"))
                        .limitExceeded(true)
                        .build();

        when(statisticsService.getBudgetStatus()).thenReturn(List.of(budgetStatus));
        when(statisticsService.getSummary()).thenReturn(summary);

        List<NotificationResponse> notifications = notificationService.getNotifications();

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("BUDGET_EXCEEDED", notifications.get(0).getType());
        assertEquals("WARNING", notifications.get(0).getSeverity());
        assertTrue(notifications.get(0).getMessage().contains("Food"));
    }

    @Test
    void getNotifications_ShouldReturnExpensesExceedIncomeNotification() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("1000.00"))
                        .totalExpense(new BigDecimal("1500.00"))
                        .balance(new BigDecimal("-500.00"))
                        .build();

        when(statisticsService.getBudgetStatus()).thenReturn(List.of());
        when(statisticsService.getSummary()).thenReturn(summary);

        List<NotificationResponse> notifications = notificationService.getNotifications();

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("EXPENSES_EXCEED_INCOME", notifications.get(0).getType());
        assertEquals("WARNING", notifications.get(0).getSeverity());
    }

    @Test
    void getNotifications_ShouldReturnMultipleNotifications() {
        SummaryResponse summary =
                SummaryResponse.builder()
                        .totalIncome(new BigDecimal("1000.00"))
                        .totalExpense(new BigDecimal("1500.00"))
                        .balance(new BigDecimal("-500.00"))
                        .build();

        BudgetStatusResponse budgetStatus =
                BudgetStatusResponse.builder()
                        .categoryId(1L)
                        .categoryName("Food")
                        .limitAmount(new BigDecimal("500.00"))
                        .spentAmount(new BigDecimal("600.00"))
                        .remainingAmount(new BigDecimal("-100.00"))
                        .limitExceeded(true)
                        .build();

        when(statisticsService.getBudgetStatus()).thenReturn(List.of(budgetStatus));
        when(statisticsService.getSummary()).thenReturn(summary);

        List<NotificationResponse> notifications = notificationService.getNotifications();

        assertNotNull(notifications);
        assertEquals(2, notifications.size());
    }
}
