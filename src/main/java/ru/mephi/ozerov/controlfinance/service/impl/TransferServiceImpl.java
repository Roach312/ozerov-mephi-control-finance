package ru.mephi.ozerov.controlfinance.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferRequest;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferResponse;
import ru.mephi.ozerov.controlfinance.entity.Transaction;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;
import ru.mephi.ozerov.controlfinance.entity.Transfer;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.repository.TransferRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.TransferService;
import ru.mephi.ozerov.controlfinance.service.UserService;
import ru.mephi.ozerov.controlfinance.service.WalletService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса переводов.
 */
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final UserService userService;

    @Override
    @Transactional
    public TransferResponse createTransfer(TransferRequest request) {
        Wallet fromWallet = walletService.getCurrentUserWalletEntity();
        String currentUserLogin = userService.getCurrentUser().getLogin();

        // Нельзя переводить самому себе
        if (currentUserLogin.equals(request.getToUserLogin())) {
            throw new ValidationException("Cannot transfer to yourself");
        }

        Wallet toWallet = walletService.getWalletByUserLogin(request.getToUserLogin());

        // Проверяем достаточность баланса отправителя
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ValidationException("Insufficient balance for transfer");
        }

        // Создаём перевод
        Transfer transfer = Transfer.builder()
                .fromWallet(fromWallet)
                .toWallet(toWallet)
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .fromUserLogin(currentUserLogin)
                .toUserLogin(request.getToUserLogin())
                .build();

        transfer = transferRepository.save(transfer);

        // Создаём транзакцию расхода для отправителя
        Transaction expenseTransaction = Transaction.builder()
                .wallet(fromWallet)
                .amount(request.getAmount())
                .type(TransactionType.EXPENSE)
                .description("Transfer to " + request.getToUserLogin())
                .createdAt(LocalDateTime.now())
                .transfer(transfer)
                .build();
        transactionRepository.save(expenseTransaction);

        // Создаём транзакцию дохода для получателя
        Transaction incomeTransaction = Transaction.builder()
                .wallet(toWallet)
                .amount(request.getAmount())
                .type(TransactionType.INCOME)
                .description("Transfer from " + currentUserLogin)
                .createdAt(LocalDateTime.now())
                .transfer(transfer)
                .build();
        transactionRepository.save(incomeTransaction);

        // Обновляем балансы
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        return mapToResponse(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getAllTransfers() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return transferRepository.findByFromWalletOrToWalletOrderByCreatedAtDesc(wallet, wallet).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getSentTransfers() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return transferRepository.findByFromWalletOrderByCreatedAtDesc(wallet).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getReceivedTransfers() {
        Wallet wallet = walletService.getCurrentUserWalletEntity();
        return transferRepository.findByToWalletOrderByCreatedAtDesc(wallet).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransferResponse mapToResponse(Transfer transfer) {
        return TransferResponse.builder()
                .id(transfer.getId())
                .fromUserLogin(transfer.getFromUserLogin())
                .toUserLogin(transfer.getToUserLogin())
                .amount(transfer.getAmount())
                .createdAt(transfer.getCreatedAt())
                .build();
    }
}
