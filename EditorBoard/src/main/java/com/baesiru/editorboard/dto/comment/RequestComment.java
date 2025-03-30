package com.baesiru.editorboard.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestComment {
    private String content;
    private String username;
    private String password;
    private Long parentId;
}
