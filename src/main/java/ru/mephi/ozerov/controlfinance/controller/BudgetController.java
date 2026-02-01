package ru.mephi.ozerov.controlfinance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetRequest;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetResponse;
import ru.mephi.ozerov.controlfinance.service.BudgetService;

import java.util.List;

/**
 * Контроллер для операций с бюджетами.
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Создать или обновить бюджет для категории.
     * @param request данные для создания/обновления бюджета
     * @return ответ бюджета
     */
    @PostMapping
    public ResponseEntity<BudgetResponse> createOrUpdateBudget(@Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createOrUpdateBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить все бюджеты текущего пользователя.
     * @return список ответов бюджетов
     */
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets() {
        List<BudgetResponse> responses = budgetService.getAllBudgets();
        return ResponseEntity.ok(responses);
    }

    /**
     * Получить бюджет для конкретной категории.
     * @param categoryId id категории
     * @return ответ бюджета или 404, если не найден
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BudgetResponse> getBudgetByCategory(@PathVariable Long categoryId) {
        BudgetResponse response = budgetService.getBudgetByCategory(categoryId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
