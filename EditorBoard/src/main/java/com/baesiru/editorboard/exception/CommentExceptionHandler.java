package com.baesiru.editorboard.exception;

import com.baesiru.editorboard.exception.comment.CommentNotFoundException;
import com.baesiru.editorboard.exception.comment.ParentCommentNotFoundException;
import com.baesiru.editorboard.exception.comment.WrongCommentPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class CommentExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<?> handleCommentNotFoundException(CommentNotFoundException e) {
        log.warn("", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(ParentCommentNotFoundException.class)
    public ResponseEntity<?> handleParentCommentNotFoundException(ParentCommentNotFoundException e) {
        log.warn("", e);
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(WrongCommentPasswordException.class)
    public ResponseEntity<?> handleWrongCommentPasswordException(WrongCommentPasswordException e) {
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
