package com.baesiru.editorboard.exception.board;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BoardNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public BoardNotFoundException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public BoardNotFoundException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public BoardNotFoundException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public BoardNotFoundException(ErrorCode errorCode, Throwable throwable,
                                   String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
