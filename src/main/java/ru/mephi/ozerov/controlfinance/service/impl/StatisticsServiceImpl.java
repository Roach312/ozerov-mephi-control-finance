package ru.mephi.ozerov.controlfinance.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.entity.*;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.BudgetRepository;
import ru.mephi.ozerov.controlfinance.repository.CategoryRepository;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.service.StatisticsService;
import ru.mephi.ozerov.controlfinance.service.UserService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

/** Реализация сервиса статистики. */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final WalletService walletService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public SummaryResponse getSummary() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();

        BigDecimal totalIncome =
                transactionRepository.sumByWalletIdAndType(wallet.getId(), TransactionType.INCOME);
        BigDecimal totalExpense =
                transactionRepository.sumByWalletIdAndType(wallet.getId(), TransactionType.EXPENSE);

        return SummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(wallet.getBalance())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getSummaryByCategories(List<Long> categoryIds) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        User user = userService.getCurrentUser();

        List<Category> categories;
        if (categoryIds == null || categoryIds.isEmpty()) {
            categories = categoryRepository.findByUser(user);
        } else {
            categories = new ArrayList<>();
            for (Long categoryId : categoryIds) {
                Category category =
                        categoryRepository
                                .findByIdAndUser(categoryId, user)
                                .orElseThrow(
                                        () ->
                                                new EntityNotFoundException(
                                                        "Category not found: " + categoryId));
                categories.add(category);
            }
        }

        return categories.stream()
                .map(
                        category -> {
                            BigDecimal totalAmount =
                                    transactionRepository.sumByWalletIdAndCategoryId(
                                            wallet.getId(), category.getId());
                            return CategorySummaryResponse.builder()
                                    .categoryId(category.getId())
                                    .categoryName(category.getName())
                                    .categoryType(category.getType())
                                    .totalAmount(totalAmount)
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetStatusResponse> getBudgetStatus() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        List<Budget> budgets = budgetRepository.findByWallet(wallet);

        return budgets.stream()
                .map(
                        budget -> {
                            BigDecimal spentAmount =
                                    transactionRepository.sumByWalletIdAndCategoryId(
                                            wallet.getId(), budget.getCategory().getId());
                            BigDecimal remainingAmount =
                                    budget.getLimitAmount().subtract(spentAmount);
                            boolean limitExceeded = remainingAmount.compareTo(BigDecimal.ZERO) < 0;

                            return BudgetStatusResponse.builder()
                                    .categoryId(budget.getCategory().getId())
                                    .categoryName(budget.getCategory().getName())
                                    .limitAmount(budget.getLimitAmount())
                                    .spentAmount(spentAmount)
                                    .remainingAmount(remainingAmount)
                                    .limitExceeded(limitExceeded)
                                    .build();
                        })
                .collect(Collectors.toList());
    }
}
