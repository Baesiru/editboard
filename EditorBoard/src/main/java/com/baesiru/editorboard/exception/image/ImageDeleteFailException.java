package com.baesiru.editorboard.exception.image;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ImageDeleteFailException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public ImageDeleteFailException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public ImageDeleteFailException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public ImageDeleteFailException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public ImageDeleteFailException(ErrorCode errorCode, Throwable throwable,
                                   String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
