package ru.mephi.ozerov.controlfinance.service;

import ru.mephi.ozerov.controlfinance.dto.notification.NotificationResponse;

import java.util.List;

/**
 * Интерфейс сервиса для операций с уведомлениями/оповещениями.
 */
public interface NotificationService {

    /**
     * Получить все уведомления/оповещения для текущего пользователя.
     * Включает предупреждения о превышении лимита бюджета и превышении расходов над доходами.
     * @return список ответов уведомлений
     */
    List<NotificationResponse> getNotifications();
}
