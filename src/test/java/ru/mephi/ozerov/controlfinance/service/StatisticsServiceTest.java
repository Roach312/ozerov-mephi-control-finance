package ru.mephi.ozerov.controlfinance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.statistics.BudgetStatusResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.CategorySummaryResponse;
import ru.mephi.ozerov.controlfinance.dto.statistics.SummaryResponse;
import ru.mephi.ozerov.controlfinance.entity.*;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.BudgetRepository;
import ru.mephi.ozerov.controlfinance.repository.CategoryRepository;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.service.impl.StatisticsServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private User user;
    private Wallet wallet;
    private Category expenseCategory;
    private Budget budget;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).login("testuser").build();

        wallet = Wallet.builder()
                .id(1L)
                .user(user)
                .balance(new BigDecimal("1000.00"))
                .build();

        expenseCategory = Category.builder()
                .id(1L)
                .user(user)
                .name("Food")
                .type(CategoryType.EXPENSE)
                .build();

        budget = Budget.builder()
                .id(1L)
                .wallet(wallet)
                .category(expenseCategory)
                .limitAmount(new BigDecimal("500.00"))
                .build();
    }

    @Test
    void getSummary_ShouldReturnSummary() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(transactionRepository.sumByWalletIdAndType(1L, TransactionType.INCOME))
                .thenReturn(new BigDecimal("2000.00"));
        when(transactionRepository.sumByWalletIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("1000.00"));

        SummaryResponse response = statisticsService.getSummary();

        assertNotNull(response);
        assertEquals(new BigDecimal("2000.00"), response.getTotalIncome());
        assertEquals(new BigDecimal("1000.00"), response.getTotalExpense());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
    }

    @Test
    void getSummaryByCategories_ShouldReturnCategorySummaries() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByUser(user)).thenReturn(List.of(expenseCategory));
        when(transactionRepository.sumByWalletIdAndCategoryId(1L, 1L)).thenReturn(new BigDecimal("300.00"));

        List<CategorySummaryResponse> responses = statisticsService.getSummaryByCategories(null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Food", responses.get(0).getCategoryName());
        assertEquals(new BigDecimal("300.00"), responses.get(0).getTotalAmount());
    }

    @Test
    void getSummaryByCategories_ShouldFilterByCategoryIds() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(transactionRepository.sumByWalletIdAndCategoryId(1L, 1L)).thenReturn(new BigDecimal("300.00"));

        List<CategorySummaryResponse> responses = statisticsService.getSummaryByCategories(List.of(1L));

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getSummaryByCategories_ShouldThrowExceptionWhenCategoryNotFound() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
                () -> statisticsService.getSummaryByCategories(List.of(99L)));
    }

    @Test
    void getBudgetStatus_ShouldReturnBudgetStatuses() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(budgetRepository.findByWallet(wallet)).thenReturn(List.of(budget));
        when(transactionRepository.sumByWalletIdAndCategoryId(1L, 1L)).thenReturn(new BigDecimal("300.00"));

        List<BudgetStatusResponse> responses = statisticsService.getBudgetStatus();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Food", responses.get(0).getCategoryName());
        assertEquals(new BigDecimal("500.00"), responses.get(0).getLimitAmount());
        assertEquals(new BigDecimal("300.00"), responses.get(0).getSpentAmount());
        assertEquals(new BigDecimal("200.00"), responses.get(0).getRemainingAmount());
        assertFalse(responses.get(0).isLimitExceeded());
    }

    @Test
    void getBudgetStatus_ShouldShowLimitExceeded() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(budgetRepository.findByWallet(wallet)).thenReturn(List.of(budget));
        when(transactionRepository.sumByWalletIdAndCategoryId(1L, 1L)).thenReturn(new BigDecimal("600.00"));

        List<BudgetStatusResponse> responses = statisticsService.getBudgetStatus();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).isLimitExceeded());
        assertEquals(new BigDecimal("-100.00"), responses.get(0).getRemainingAmount());
    }
}
