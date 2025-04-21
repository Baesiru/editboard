package com.baesiru.editorboard.exception.comment;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class WrongCommentPasswordException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public WrongCommentPasswordException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public WrongCommentPasswordException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public WrongCommentPasswordException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public WrongCommentPasswordException(ErrorCode errorCode, Throwable throwable,
                                         String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
