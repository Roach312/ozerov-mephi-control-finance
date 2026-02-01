package ru.mephi.ozerov.controlfinance.exception;

/**
 * Исключение, выбрасываемое при попытке создать сущность, которая уже существует.
 */
public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
