package com.baesiru.editorboard.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseComment {
    private Long id;
    private String content;
    private String username;
    private Long parentId;
    private Long depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}