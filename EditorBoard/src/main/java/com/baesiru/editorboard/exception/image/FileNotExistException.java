package com.baesiru.editorboard.exception.image;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;

@Getter
public class FileNotExistException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description;

    public FileNotExistException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public FileNotExistException(ErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.description = errorDescription;
    }

    public FileNotExistException(ErrorCode errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }

    public FileNotExistException(ErrorCode errorCode, Throwable throwable,
                                  String errorDescription) {
        super(throwable);
        this.errorCode = errorCode;
        this.description = errorDescription;
    }
}
