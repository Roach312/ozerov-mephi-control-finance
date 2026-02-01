package ru.mephi.ozerov.controlfinance.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferRequest;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferResponse;
import ru.mephi.ozerov.controlfinance.service.TransferService;

/** Контроллер для операций с переводами. */
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    /**
     * Создать новый перевод другому пользователю.
     *
     * @param request данные для создания перевода
     * @return ответ созданного перевода
     */
    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить все переводы текущего пользователя (отправленные и полученные).
     *
     * @return список ответов переводов
     */
    @GetMapping
    public ResponseEntity<List<TransferResponse>> getTransfers() {
        List<TransferResponse> responses = transferService.getAllTransfers();
        return ResponseEntity.ok(responses);
    }

    /**
     * Получить переводы, отправленные текущим пользователем.
     *
     * @return список ответов переводов
     */
    @GetMapping("/sent")
    public ResponseEntity<List<TransferResponse>> getSentTransfers() {
        List<TransferResponse> responses = transferService.getSentTransfers();
        return ResponseEntity.ok(responses);
    }

    /**
     * Получить переводы, полученные текущим пользователем.
     *
     * @return список ответов переводов
     */
    @GetMapping("/received")
    public ResponseEntity<List<TransferResponse>> getReceivedTransfers() {
        List<TransferResponse> responses = transferService.getReceivedTransfers();
        return ResponseEntity.ok(responses);
    }
}
