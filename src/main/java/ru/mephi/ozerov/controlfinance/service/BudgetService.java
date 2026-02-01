package ru.mephi.ozerov.controlfinance.service;

import ru.mephi.ozerov.controlfinance.dto.budget.BudgetRequest;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetResponse;

import java.util.List;

/**
 * Интерфейс сервиса для операций с бюджетами.
 */
public interface BudgetService {

    /**
     * Создать или обновить бюджет для категории.
     * @param request данные для создания/обновления бюджета
     * @return ответ бюджета
     */
    BudgetResponse createOrUpdateBudget(BudgetRequest request);

    /**
     * Получить все бюджеты кошелька текущего пользователя.
     * @return список ответов бюджетов
     */
    List<BudgetResponse> getAllBudgets();

    /**
     * Получить бюджет по id категории.
     * @param categoryId id категории
     * @return ответ бюджета или null, если не найден
     */
    BudgetResponse getBudgetByCategory(Long categoryId);
}
