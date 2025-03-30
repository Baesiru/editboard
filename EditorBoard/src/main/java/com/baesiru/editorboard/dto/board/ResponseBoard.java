package com.baesiru.editorboard.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBoard {
    private Long id;
    private String title;
    private String content;
    private String username;
    private String viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
