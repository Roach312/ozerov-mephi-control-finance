package ru.mephi.ozerov.controlfinance.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionTest {

    @Test
    void entityNotFoundException_WithMessage() {
        EntityNotFoundException exception = new EntityNotFoundException("Not found");
        assertEquals("Not found", exception.getMessage());
    }

    @Test
    void entityNotFoundException_WithMessageAndCause() {
        Throwable cause = new RuntimeException("Original error");
        EntityNotFoundException exception = new EntityNotFoundException("Not found", cause);
        assertEquals("Not found", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void entityAlreadyExistsException_WithMessage() {
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Already exists");
        assertEquals("Already exists", exception.getMessage());
    }

    @Test
    void entityAlreadyExistsException_WithMessageAndCause() {
        Throwable cause = new RuntimeException("Original error");
        EntityAlreadyExistsException exception =
                new EntityAlreadyExistsException("Already exists", cause);
        assertEquals("Already exists", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void validationException_WithMessage() {
        ValidationException exception = new ValidationException("Validation failed");
        assertEquals("Validation failed", exception.getMessage());
    }

    @Test
    void validationException_WithMessageAndCause() {
        Throwable cause = new RuntimeException("Original error");
        ValidationException exception = new ValidationException("Validation failed", cause);
        assertEquals("Validation failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void errorResponse_Builder() {
        LocalDateTime now = LocalDateTime.now();
        List<ErrorResponse.FieldError> fieldErrors =
                Arrays.asList(
                        ErrorResponse.FieldError.builder()
                                .field("name")
                                .message("Name is required")
                                .build());

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(now)
                        .status(400)
                        .error("Bad Request")
                        .message("Validation failed")
                        .path("/api/test")
                        .fieldErrors(fieldErrors)
                        .build();

        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Validation failed", response.getMessage());
        assertEquals("/api/test", response.getPath());
        assertEquals(1, response.getFieldErrors().size());
        assertEquals("name", response.getFieldErrors().get(0).getField());
        assertEquals("Name is required", response.getFieldErrors().get(0).getMessage());
    }

    @Test
    void errorResponse_NoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNull(response.getMessage());
    }

    @Test
    void errorResponse_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<ErrorResponse.FieldError> fieldErrors =
                Arrays.asList(new ErrorResponse.FieldError("field", "error"));

        ErrorResponse response =
                new ErrorResponse(
                        now,
                        500,
                        "Internal Server Error",
                        "Something went wrong",
                        "/api/error",
                        fieldErrors);

        assertEquals(500, response.getStatus());
        assertEquals("Something went wrong", response.getMessage());
    }

    @Test
    void errorResponse_SettersAndGetters() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setTimestamp(now);
        response.setStatus(404);
        response.setError("Not Found");
        response.setMessage("Resource not found");
        response.setPath("/api/resource");
        response.setFieldErrors(null);

        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
        assertEquals("/api/resource", response.getPath());
        assertNull(response.getFieldErrors());
    }

    @Test
    void fieldError_NoArgsConstructor() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError();
        assertNull(fieldError.getField());
    }

    @Test
    void fieldError_SettersAndGetters() {
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError();
        fieldError.setField("username");
        fieldError.setMessage("Username is required");

        assertEquals("username", fieldError.getField());
        assertEquals("Username is required", fieldError.getMessage());
    }

    @Test
    void errorResponse_EqualsAndHashCode() {
        ErrorResponse response1 = ErrorResponse.builder().status(400).message("Error").build();
        ErrorResponse response2 = ErrorResponse.builder().status(400).message("Error").build();

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void fieldError_EqualsAndHashCode() {
        ErrorResponse.FieldError error1 =
                ErrorResponse.FieldError.builder().field("name").message("Required").build();
        ErrorResponse.FieldError error2 =
                ErrorResponse.FieldError.builder().field("name").message("Required").build();

        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void errorResponse_ToString() {
        ErrorResponse response = ErrorResponse.builder().status(400).message("Error").build();
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("400"));
    }

    @Test
    void fieldError_ToString() {
        ErrorResponse.FieldError error =
                ErrorResponse.FieldError.builder().field("name").message("Required").build();
        assertNotNull(error.toString());
        assertTrue(error.toString().contains("name"));
    }
}
