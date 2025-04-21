package com.baesiru.editorboard.exception.image;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class FolderCreationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public FolderCreationException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public FolderCreationException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public FolderCreationException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public FolderCreationException(ErrorCode errorCode, Throwable throwable,
                                 String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
