package ru.mephi.ozerov.controlfinance.exception;

/** Исключение, выбрасываемое при ошибке валидации. */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
