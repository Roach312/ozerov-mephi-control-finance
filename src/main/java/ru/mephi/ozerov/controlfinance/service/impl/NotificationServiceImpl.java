package ru.mephi.ozerov.controlfinance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.notification.NotificationResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.service.NotificationService;
import ru.mephi.ozerov.controlfinance.service.StatisticsService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса уведомлений.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final StatisticsService statisticsService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {
        List<NotificationResponse> notifications = new ArrayList<>();

        // Проверяем превышение лимита бюджета
        List<BudgetStatusResponse> budgetStatuses = statisticsService.getBudgetStatus();
        for (BudgetStatusResponse status : budgetStatuses) {
            if (status.isLimitExceeded()) {
                notifications.add(NotificationResponse.builder()
                        .type("BUDGET_EXCEEDED")
                        .message("Budget limit exceeded for category '" + status.getCategoryName() + 
                                "'. Spent: " + status.getSpentAmount() + 
                                ", Limit: " + status.getLimitAmount())
                        .severity("WARNING")
                        .build());
            }
        }

        // Проверяем, превышают ли расходы доходы
        SummaryResponse summary = statisticsService.getSummary();
        if (summary.getTotalExpense().compareTo(summary.getTotalIncome()) > 0) {
            BigDecimal difference = summary.getTotalExpense().subtract(summary.getTotalIncome());
            notifications.add(NotificationResponse.builder()
                    .type("EXPENSES_EXCEED_INCOME")
                    .message("Your expenses exceed your income by " + difference)
                    .severity("WARNING")
                    .build());
        }

        return notifications;
    }
}
