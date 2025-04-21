package com.baesiru.editorboard.exception.image;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ImageNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public ImageNotFoundException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public ImageNotFoundException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public ImageNotFoundException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public ImageNotFoundException(ErrorCode errorCode, Throwable throwable,
                                    String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
