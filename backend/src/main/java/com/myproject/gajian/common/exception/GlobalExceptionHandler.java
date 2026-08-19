package com.myproject.gajian.common.exception;

import com.myproject.gajian.common.response.ApiErrorResponse;
import com.myproject.gajian.common.response.ApiErrorResponse.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * Extends {@link ResponseEntityExceptionHandler} so Spring MVC's own failures (unreadable body, wrong
 * method, wrong media type, unmapped path) keep their real status instead of being swallowed by the
 * {@code Exception} catch-all and reported as 500; the inherited handlers are re-rendered through the
 * shared envelope by {@link #handleExceptionInternal}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception) {
        return toResponse(exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<ApiErrorResponse> handleFailedLogin() {
        // DisabledException is deliberately flattened into INVALID_CREDENTIALS: the disabled check runs
        // before the password check, so a distinct code would tell an anonymous caller which emails exist.
        return toResponse(ErrorCode.INVALID_CREDENTIALS, ErrorCode.INVALID_CREDENTIALS.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("Unhandled exception", exception);
        return toResponse(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getDefaultMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<ValidationError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(ApiErrorResponse.validation(ErrorCode.VALIDATION_FAILED.getDefaultMessage(), errors));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        ErrorCode errorCode = errorCodeFor(statusCode);
        return ResponseEntity.status(statusCode)
                .body(ApiErrorResponse.of(errorCode.getDefaultMessage(), errorCode.name()));
    }

    private static ErrorCode errorCodeFor(HttpStatusCode statusCode) {
        return switch (HttpStatus.valueOf(statusCode.value())) {
            case BAD_REQUEST -> ErrorCode.MALFORMED_REQUEST;
            case NOT_FOUND -> ErrorCode.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> ErrorCode.METHOD_NOT_ALLOWED;
            case UNSUPPORTED_MEDIA_TYPE -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }

    private ResponseEntity<ApiErrorResponse> toResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiErrorResponse.of(message, errorCode.name()));
    }
}
