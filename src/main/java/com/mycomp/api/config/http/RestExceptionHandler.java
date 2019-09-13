package com.mycomp.api.config.http;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Daniel
 */
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ApiError handleException(BadCredentialsException ex) {
        return new ApiError(ex.getMessage(), ApiError.ApiErrorType.AUTHORIZATION);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiError handleException(Exception ex) {
        return new ApiError(ex.getMessage(), ApiError.ApiErrorType.API_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<List<String>> handleTransactionException(TransactionSystemException ex) throws Throwable {
        Throwable cause = ex.getCause();
        if (!(cause instanceof RollbackException)) {
            throw cause;
        }
        if (!(cause.getCause() instanceof ConstraintViolationException)) {
            throw cause.getCause();
        }
        ConstraintViolationException validationException = (ConstraintViolationException) cause.getCause();
        List<String> messages = validationException.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.toList());
        return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
    }
}
