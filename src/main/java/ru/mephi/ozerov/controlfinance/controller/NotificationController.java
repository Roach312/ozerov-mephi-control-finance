package ru.mephi.ozerov.controlfinance.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.ozerov.controlfinance.dto.notification.NotificationResponse;
import ru.mephi.ozerov.controlfinance.service.NotificationService;

/** Контроллер для операций с уведомлениями. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Получить все уведомления/оповещения для текущего пользователя.
     *
     * @return список ответов уведомлений
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        List<NotificationResponse> responses = notificationService.getNotifications();
        return ResponseEntity.ok(responses);
    }
}
