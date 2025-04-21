package com.baesiru.editorboard.exception.image;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    IMAGE_NOT_FOUND(404, "이미지를 찾을 수 없습니다."),
    FILE_NOT_EXIST(404, "이미지 파일이 존재하지 않습니다."),
    FOLDER_CREATE_ERROR(500, "폴더를 생성할 수 없습니다.");
    private final Integer httpCode;
    private final String description;
}
