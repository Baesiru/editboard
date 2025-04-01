package com.baesiru.editorboard.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentUpdate {
    @NotBlank(message = "필수 입력 항목입니다.")
    private String content;
    @NotBlank(message = "필수 입력 항목입니다.")
    @Size(min = 2, max = 100, message = "최소 2자, 최대 100자까지 입력 가능합니다.")
    private String password;
}