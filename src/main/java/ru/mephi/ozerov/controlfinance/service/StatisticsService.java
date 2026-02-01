package ru.mephi.ozerov.controlfinance.service;

import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;

import java.util.List;

/**
 * Интерфейс сервиса для операций со статистикой.
 */
public interface StatisticsService {

    /**
     * Получить финансовую сводку для текущего пользователя.
     * @return ответ сводки с итогами
     */
    SummaryResponse getSummary();

    /**
     * Получить сводку по категориям для текущего пользователя.
     * @param categoryIds опциональный список id категорий для фильтрации
     * @return список ответов сводки по категориям
     */
    List<CategorySummaryResponse> getSummaryByCategories(List<Long> categoryIds);

    /**
     * Получить статус бюджета для всех категорий с бюджетами.
     * @return список ответов статуса бюджета
     */
    List<BudgetStatusResponse> getBudgetStatus();
}
