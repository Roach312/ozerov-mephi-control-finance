package ru.mephi.ozerov.controlfinance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.service.StatisticsService;

import java.util.List;

/**
 * Контроллер для операций со статистикой.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Получить финансовую сводку для текущего пользователя.
     * @return ответ сводки с итогами
     */
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary() {
        SummaryResponse response = statisticsService.getSummary();
        return ResponseEntity.ok(response);
    }

    /**
     * Получить сводку по категориям для текущего пользователя.
     * @param categoryIds опциональный список id категорий для фильтрации
     * @return список ответов сводки по категориям
     */
    @GetMapping("/by-categories")
    public ResponseEntity<List<CategorySummaryResponse>> getSummaryByCategories(
            @RequestParam(required = false) List<Long> categoryIds) {
        List<CategorySummaryResponse> responses = statisticsService.getSummaryByCategories(categoryIds);
        return ResponseEntity.ok(responses);
    }

    /**
     * Получить статус бюджета для всех категорий с бюджетами.
     * @return список ответов статуса бюджета
     */
    @GetMapping("/budget-status")
    public ResponseEntity<List<BudgetStatusResponse>> getBudgetStatus() {
        List<BudgetStatusResponse> responses = statisticsService.getBudgetStatus();
        return ResponseEntity.ok(responses);
    }
}
