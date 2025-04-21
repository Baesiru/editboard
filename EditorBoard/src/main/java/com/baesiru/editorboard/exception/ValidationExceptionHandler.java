package com.baesiru.editorboard.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        log.warn("", e);
        HttpStatusCode httpStatusCode = e.getStatusCode();
        return handleExceptionInternal(httpStatusCode, builder.toString());
    }

    private ResponseEntity<?> handleExceptionInternal(HttpStatusCode httpStatusCode, String detailMessage) {
        return ResponseEntity.status(httpStatusCode)
                .body(makeErrorResponse(httpStatusCode, detailMessage));
    }

    private ExceptionResponse makeErrorResponse(HttpStatusCode httpStatusCode, String detailMessage) {
        return ExceptionResponse.builder()
                .httpCode(httpStatusCode.value())
                .message(detailMessage)
                .build();
    }
}
