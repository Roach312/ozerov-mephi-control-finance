package ru.mephi.ozerov.controlfinance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionRequest;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.*;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.impl.TransactionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Wallet wallet;
    private Category expenseCategory;
    private Category incomeCategory;
    private Transaction transaction;

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

        incomeCategory = Category.builder()
                .id(2L)
                .user(user)
                .name("Salary")
                .type(CategoryType.INCOME)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .wallet(wallet)
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.EXPENSE)
                .category(expenseCategory)
                .description("Lunch")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createTransaction_ShouldCreateExpenseTransaction() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.EXPENSE)
                .categoryId(1L)
                .description("Lunch")
                .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(expenseCategory);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(TransactionType.EXPENSE, response.getType());
        verify(transactionRepository).save(any(Transaction.class));
        verify(walletRepository).save(wallet);
        assertEquals(new BigDecimal("900.00"), wallet.getBalance());
    }

    @Test
    void createTransaction_ShouldCreateIncomeTransaction() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.INCOME)
                .categoryId(2L)
                .description("Monthly salary")
                .build();

        Transaction incomeTransaction = Transaction.builder()
                .id(2L)
                .wallet(wallet)
                .amount(new BigDecimal("500.00"))
                .type(TransactionType.INCOME)
                .category(incomeCategory)
                .createdAt(LocalDateTime.now())
                .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(2L)).thenReturn(incomeCategory);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(incomeTransaction);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(TransactionType.INCOME, response.getType());
        assertEquals(new BigDecimal("1500.00"), wallet.getBalance());
    }

    @Test
    void createTransaction_ShouldThrowExceptionForMismatchedTypes() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .categoryId(1L)
                .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(expenseCategory);

        assertThrows(ValidationException.class, () -> transactionService.createTransaction(request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(transactionRepository.findByWalletOrderByCreatedAtDesc(wallet)).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getAllTransactions();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getTransactionsByType_ShouldReturnFilteredTransactions() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(transactionRepository.findByWalletAndType(wallet, TransactionType.EXPENSE)).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getTransactionsByType(TransactionType.EXPENSE);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TransactionType.EXPENSE, responses.get(0).getType());
    }

    @Test
    void getTransactionsByCategory_ShouldReturnFilteredTransactions() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(wallet);
        when(categoryService.getCategoryById(1L)).thenReturn(expenseCategory);
        when(transactionRepository.findByWalletAndCategory(wallet, expenseCategory)).thenReturn(List.of(transaction));

        List<TransactionResponse> responses = transactionService.getTransactionsByCategory(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
}
