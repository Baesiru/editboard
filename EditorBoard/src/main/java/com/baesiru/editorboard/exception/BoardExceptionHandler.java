package com.baesiru.editorboard.exception;

import com.baesiru.editorboard.exception.board.BoardNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class BoardExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<?> handleBoardNotFoundException(BoardNotFoundException e) {
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
                .message(errorCode.getMessage())
                .build();
    }
}
