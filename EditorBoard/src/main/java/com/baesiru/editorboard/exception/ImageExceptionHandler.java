package com.baesiru.editorboard.exception;

import com.baesiru.editorboard.exception.image.FileNotExistException;
import com.baesiru.editorboard.exception.image.FolderCreationException;
import com.baesiru.editorboard.exception.image.ImageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ImageExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<?> handleImageNotFoundException(ImageNotFoundException e) {
        log.warn("", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(FileNotExistException.class)
    public ResponseEntity<?> handleFileNotExistException(FileNotExistException e) {
        log.warn("", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(FolderCreationException.class)
    public ResponseEntity<?> handleFileNotExistException(FolderCreationException e) {
        log.warn("", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<?> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpCode())
                .body(makeErrorResponse(errorCode));
    }

    private ExceptionResponse makeErrorResponse(ErrorCode errorCode) {
        return ExceptionResponse.builder()
                .httpCode(errorCode.getHttpCode())
                .message(errorCode.getDescription())
                .build();
    }
}
