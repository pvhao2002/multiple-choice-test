package com.app.quizzservice.exception;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.enums.ErrorCodeEnum;
import lombok.extern.java.Log;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.logging.Level;

@Log
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public Object handleAppException(AppException e) {
        return ResponseContainer.failure(e.getErrorCode(), e.getErrorMessage(), e.getCustomMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationException(MethodArgumentNotValidException exception) {
        var message = exception.getBindingResult()
                               .getFieldErrors()
                               .stream()
                               .findFirst()
                               .map(FieldError::getDefaultMessage)
                               .orElse("");
        return ResponseContainer.failure(ErrorCodeEnum.INVALID_INPUT, message);
    }

    @ExceptionHandler(Exception.class)
    public Object handleUnwantedException(Exception e) {
        log.log(Level.WARNING, "Exception >> GlobalExceptionHandler >> handleUnwantedException: {0}", e);
        return ResponseContainer.failure(ErrorCodeEnum.SERVER_ERROR, e.getMessage());
    }
}
