package com.baesiru.editorboard.exception.comment;

import com.baesiru.editorboard.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND(404, "해당 댓글의 상위 댓글을 찾을 수 없습니다."),
    WRONG_COMMENT_PASSWORD(401, "댓글의 비밀번호가 틀렸습니다."),

    ;
    private final Integer httpCode;
    private final String description;
}
