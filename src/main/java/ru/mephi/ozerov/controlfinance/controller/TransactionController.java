package ru.mephi.ozerov.controlfinance.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionRequest;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;
import ru.mephi.ozerov.controlfinance.service.TransactionService;

/** Контроллер для операций с транзакциями. */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Создать новую транзакцию.
     *
     * @param request данные для создания транзакции
     * @return ответ созданной транзакции
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить все транзакции текущего пользователя.
     *
     * @param type опциональный фильтр по типу транзакции
     * @param categoryId опциональный фильтр по категории
     * @return список ответов транзакций
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId) {
        List<TransactionResponse> responses;
        if (categoryId != null) {
            responses = transactionService.getTransactionsByCategory(categoryId);
        } else if (type != null) {
            responses = transactionService.getTransactionsByType(type);
        } else {
            responses = transactionService.getAllTransactions();
        }
        return ResponseEntity.ok(responses);
    }
}
