package uk.gov.hmcts.tasks.dev.controllers.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(
            error.getField(),
            error.getDefaultMessage()
        ));

        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, "Validation Failed");
        response.put("details", errors);

        log.error("Validation error [status={}]: {}", HttpStatus.BAD_REQUEST.value(), errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, "Invalid Input");
        response.put(MESSAGE, ex.getMessage());

        log.error("Illegal argument error [status={}]: {}", HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        response.put(ERROR, "Resource Not Found");
        response.put(MESSAGE, ex.getMessage());

        log.error("Resource not found [status={}]: {}", HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR, "Internal Server Error");
        response.put(MESSAGE, ex.getMessage());

        log.error("Unexpected error [status={}]: {}", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
