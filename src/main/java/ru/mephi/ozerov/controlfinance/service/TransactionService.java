package ru.mephi.ozerov.controlfinance.service;

import java.util.List;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionRequest;
import ru.mephi.ozerov.controlfinance.dto.transaction.TransactionResponse;
import ru.mephi.ozerov.controlfinance.entity.TransactionType;

/** Интерфейс сервиса для операций с транзакциями. */
public interface TransactionService {

    /**
     * Создать новую транзакцию.
     *
     * @param request данные для создания транзакции
     * @return ответ транзакции
     */
    TransactionResponse createTransaction(TransactionRequest request);

    /**
     * Получить все транзакции текущего пользователя.
     *
     * @return список ответов транзакций
     */
    List<TransactionResponse> getAllTransactions();

    /**
     * Получить транзакции по типу.
     *
     * @param type фильтр по типу транзакции
     * @return список ответов транзакций
     */
    List<TransactionResponse> getTransactionsByType(TransactionType type);

    /**
     * Получить транзакции по категории.
     *
     * @param categoryId фильтр по id категории
     * @return список ответов транзакций
     */
    List<TransactionResponse> getTransactionsByCategory(Long categoryId);
}
