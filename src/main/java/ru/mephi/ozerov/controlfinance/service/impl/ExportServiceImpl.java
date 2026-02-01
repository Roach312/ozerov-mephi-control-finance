package ru.mephi.ozerov.controlfinance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.service.ExportService;
import ru.mephi.ozerov.controlfinance.service.StatisticsService;
import ru.mephi.ozerov.controlfinance.service.TransactionService;

/** Реализация сервиса экспорта. */
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final StatisticsService statisticsService;
    private final TransactionService transactionService;

    @Override
    @Transactional(readOnly = true)
    public String exportSummaryAsText() {
        SummaryResponse summary = statisticsService.getSummary();
        List<CategorySummaryResponse> categorySummaries =
                statisticsService.getSummaryByCategories(null);
        List<BudgetStatusResponse> budgetStatuses = statisticsService.getBudgetStatus();
        List<TransactionResponse> transactions = transactionService.getAllTransactions();

        StringBuilder sb = new StringBuilder();
        sb.append("=== FINANCIAL SUMMARY ===\n\n");
        sb.append("Total Income: ").append(summary.getTotalIncome()).append("\n");
        sb.append("Total Expense: ").append(summary.getTotalExpense()).append("\n");
        sb.append("Current Balance: ").append(summary.getBalance()).append("\n\n");

        sb.append("=== CATEGORIES ===\n\n");
        for (CategorySummaryResponse cs : categorySummaries) {
            sb.append(cs.getCategoryName())
                    .append(" (")
                    .append(cs.getCategoryType())
                    .append("): ")
                    .append(cs.getTotalAmount())
                    .append("\n");
        }

        sb.append("\n=== BUDGET STATUS ===\n\n");
        for (BudgetStatusResponse bs : budgetStatuses) {
            sb.append(bs.getCategoryName())
                    .append(": Limit=")
                    .append(bs.getLimitAmount())
                    .append(", Spent=")
                    .append(bs.getSpentAmount())
                    .append(", Remaining=")
                    .append(bs.getRemainingAmount())
                    .append(bs.isLimitExceeded() ? " [EXCEEDED]" : "")
                    .append("\n");
        }

        sb.append("\n=== RECENT TRANSACTIONS ===\n\n");
        int count = 0;
        for (TransactionResponse t : transactions) {
            if (count >= 10) break;
            sb.append(t.getCreatedAt())
                    .append(" | ")
                    .append(t.getType())
                    .append(" | ")
                    .append(t.getAmount())
                    .append(" | ")
                    .append(t.getCategoryName() != null ? t.getCategoryName() : "Transfer")
                    .append(" | ")
                    .append(t.getDescription() != null ? t.getDescription() : "")
                    .append("\n");
            count++;
        }

        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportSummaryAsJson() {
        SummaryResponse summary = statisticsService.getSummary();
        List<CategorySummaryResponse> categorySummaries =
                statisticsService.getSummaryByCategories(null);
        List<BudgetStatusResponse> budgetStatuses = statisticsService.getBudgetStatus();
        List<TransactionResponse> transactions = transactionService.getAllTransactions();

        Map<String, Object> exportData = new HashMap<>();
        exportData.put("summary", summary);
        exportData.put("categories", categorySummaries);
        exportData.put("budgetStatus", budgetStatuses);
        exportData.put(
                "recentTransactions",
                transactions.size() > 10 ? transactions.subList(0, 10) : transactions);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to export data\"}";
        }
    }
}
