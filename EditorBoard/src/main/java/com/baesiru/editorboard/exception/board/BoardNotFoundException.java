package com.baesiru.editorboard.exception.board;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BoardNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    public BoardNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
