package ru.mephi.ozerov.controlfinance.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetRequest;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetResponse;
import ru.mephi.ozerov.controlfinance.entity.Budget;
import ru.mephi.ozerov.controlfinance.entity.Category;
import ru.mephi.ozerov.controlfinance.entity.CategoryType;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.BudgetRepository;
import ru.mephi.ozerov.controlfinance.service.BudgetService;
import ru.mephi.ozerov.controlfinance.service.CategoryService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

/** Реализация сервиса бюджетов. */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public BudgetResponse createOrUpdateBudget(BudgetRequest request) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        Category category = categoryService.getCategoryById(request.getCategoryId());

        // Проверяем, что категория является категорией расходов
        if (category.getType() != CategoryType.EXPENSE) {
            throw new ValidationException("Budget can only be set for expense categories");
        }

        Optional<Budget> existingBudget =
                budgetRepository.findByWalletAndCategory(wallet, category);

        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setLimitAmount(request.getLimitAmount());
        } else {
            budget =
                    Budget.builder()
                            .wallet(wallet)
                            .category(category)
                            .limitAmount(request.getLimitAmount())
                            .build();
        }

        budget = budgetRepository.save(budget);

        return mapToResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return budgetRepository.findByWallet(wallet).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetByCategory(Long categoryId) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        Category category = categoryService.getCategoryById(categoryId);

        return budgetRepository
                .findByWalletAndCategory(wallet, category)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .limitAmount(budget.getLimitAmount())
                .build();
    }
}
