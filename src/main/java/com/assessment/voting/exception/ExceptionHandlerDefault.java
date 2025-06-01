package com.assessment.voting.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionHandlerDefault {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Error> handleBusinessException(BusinessException exception, ServerWebExchange exchange) {
        log.error("BusinessException: {}", exception.getMessage());
        return buildError(exception.getMessage(), null, HttpStatus.BAD_REQUEST, exchange);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> handleNotFoundException(NotFoundException exception, ServerWebExchange exchange) {
        log.error("Error Not found: {}", exception.getMessage());
        return buildError(exception.getMessage(), null, HttpStatus.NOT_FOUND, exchange);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<Error> handleMethodNotAllowedException(MethodNotAllowedException exception, ServerWebExchange exchange) {
        log.error("Error: {}", exception.getMessage());
        return buildError(exception.getMessage(), null, HttpStatus.METHOD_NOT_ALLOWED, exchange);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, ServerWebExchange exchange) {
        log.error("Error handleMethodArgumentNotValidException: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.merge(fieldName, errorMessage, (existing, newMessage) -> existing + "; " + newMessage);
        });

        return buildError("Some fields are invalid", errors, HttpStatus.BAD_REQUEST, exchange);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Error> handleServerWebInputException(ServerWebInputException exception, ServerWebExchange exchange) {
        log.error("Error handleServerWebInputException: {}", exception.getMessage());
        String errorMsg;
        Map<String, String> errors = new HashMap<>();

        if (exception instanceof WebExchangeBindException webExchangeBindException) {
            webExchangeBindException.getBindingResult().getAllErrors().forEach(error -> {
                if (error instanceof FieldError fieldError) {
                    String fieldName = fieldError.getField();
                    String errorMessage = fieldError.getDefaultMessage();
                    errors.merge(fieldName, errorMessage, (existing, newMessage) -> existing + "; " + newMessage);
                }
            });
            errorMsg = "Some fields are invalid";

        } else {
            String detail = exception.getBody().getDetail();
            String parameterName = exception.getMethodParameter() != null
                    ? exception.getMethodParameter().getParameter().getName()
                    : "unknown";
            errorMsg = String.format(" Error %s On %s", detail, parameterName);
        }
        return buildError(errorMsg, errors, HttpStatus.BAD_REQUEST, exchange);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception exception, ServerWebExchange exchange) {
        log.error("Error generic: {}", exception.getMessage());
        return buildError(exception.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, exchange);
    }

    private ResponseEntity<Error> buildError(String message, Map<String, String> fields, HttpStatus status, ServerWebExchange request) {
        var error = new Error(message,
                status.value(),
                request.getRequest().getURI().getPath(),
                request.getRequest().getMethod().name(),
                fields);

        return ResponseEntity.status(status).body(error);
    }
}
