package com.baesiru.editorboard.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBoard {
    private String title;
    private String content;
    private String username;
    private String password;
}
