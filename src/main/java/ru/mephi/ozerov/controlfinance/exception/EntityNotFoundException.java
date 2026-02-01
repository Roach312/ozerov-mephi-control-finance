package ru.mephi.ozerov.controlfinance.exception;

/**
 * Исключение, выбрасываемое когда запрошенная сущность не найдена.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
