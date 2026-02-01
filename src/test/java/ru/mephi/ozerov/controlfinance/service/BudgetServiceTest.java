package ru.mephi.ozerov.controlfinance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetRequest;
import ru.mephi.ozerov.controlfinance.dto.budget.BudgetResponse;
import ru.mephi.ozerov.controlfinance.entity.*;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.BudgetRepository;
import ru.mephi.ozerov.controlfinance.service.impl.BudgetServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User user;
    private Wallet wallet;
    private Category category;
    private Budget budget;
    private BudgetRequest budgetRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).login("testuser").build();

        wallet = Wallet.builder()
                .id(1L)
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        category = Category.builder()
                .id(1L)
                .user(user)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .build();

        budget = Budget.builder()
                .id(1L)
                .wallet(wallet)
                .category(category)
                .limitAmount(new BigDecimal("500.00"))
                .build();

        budgetRequest = BudgetRequest.builder()
                .categoryId(1L)
                .limitAmount(new BigDecimal("500.00"))
                .build();
    }

    @Test
    void createOrUpdateBudget_ShouldCreateNewBudget() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(budgetRepository.findByWalletAndCategory(wallet, category)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        BudgetResponse response = budgetService.createOrUpdateBudget(budgetRequest);

        assertNotNull(response);
        assertEquals(new BigDecimal("500.00"), response.getLimitAmount());
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void createOrUpdateBudget_ShouldUpdateExistingBudget() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(budgetRepository.findByWalletAndCategory(wallet, category)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        BudgetResponse response = budgetService.createOrUpdateBudget(budgetRequest);

        assertNotNull(response);
        verify(budgetRepository).save(budget);
    }

    @Test
    void createOrUpdateBudget_ShouldThrowExceptionForIncomeCategory() {
        Category incomeCategory = Category.builder()
                .id(2L)
                .user(user)
                .name("Salary")
                .type(CategoryType.INCOME)
                .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(incomeCategory);

        assertThrows(ValidationException.class, () -> budgetService.createOrUpdateBudget(budgetRequest));
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void getAllBudgets_ShouldReturnAllBudgets() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(budgetRepository.findByWallet(wallet)).thenReturn(List.of(budget));

        List<BudgetResponse> responses = budgetService.getAllBudgets();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getBudgetByCategory_ShouldReturnBudget() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(budgetRepository.findByWalletAndCategory(wallet, category)).thenReturn(Optional.of(budget));

        BudgetResponse response = budgetService.getBudgetByCategory(1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("500.00"), response.getLimitAmount());
    }

    @Test
    void getBudgetByCategory_ShouldReturnNullWhenNotFound() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(budgetRepository.findByWalletAndCategory(wallet, category)).thenReturn(Optional.empty());

        BudgetResponse response = budgetService.getBudgetByCategory(1L);

        assertNull(response);
    }
}
