package ru.mephi.ozerov.controlfinance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionRequest;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.*;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.CategoryService;
import ru.mephi.ozerov.controlfinance.service.TransactionService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса транзакций.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        Category category = categoryService.getCategoryById(request.getCategoryId());

        // Проверяем соответствие типа категории типу транзакции
        if (request.getType() == TransactionType.INCOME && category.getType() != CategoryType.INCOME) {
            throw new ValidationException("Income transactions require an income category");
        }
        if (request.getType() == TransactionType.EXPENSE && category.getType() != CategoryType.EXPENSE) {
            throw new ValidationException("Expense transactions require an expense category");
        }

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .type(request.getType())
                .category(category)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        // Обновляем баланс кошелька
        if (request.getType() == TransactionType.INCOME) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        } else {
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        }
        walletRepository.save(wallet);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByType(TransactionType type) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return transactionRepository.findByWalletAndType(wallet, type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByCategory(Long categoryId) {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findByWalletAndCategory(wallet, category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .transferId(transaction.getTransfer() != null ? transaction.getTransfer().getId() : null)
                .build();
    }
}
