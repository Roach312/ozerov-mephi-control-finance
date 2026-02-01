package ru.mephi.ozerov.controlfinance.service;

import java.util.List;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferRequest;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferResponse;

/** Интерфейс сервиса для операций с переводами. */
public interface TransferService {

    /**
     * Создать новый перевод другому пользователю.
     *
     * @param request данные для создания перевода
     * @return ответ перевода
     */
    TransferResponse createTransfer(TransferRequest request);

    /**
     * Получить все переводы текущего пользователя (отправленные и полученные).
     *
     * @return список ответов переводов
     */
    List<TransferResponse> getAllTransfers();

    /**
     * Получить переводы, отправленные текущим пользователем.
     *
     * @return список ответов переводов
     */
    List<TransferResponse> getSentTransfers();

    /**
     * Получить переводы, полученные текущим пользователем.
     *
     * @return список ответов переводов
     */
    List<TransferResponse> getReceivedTransfers();
}
