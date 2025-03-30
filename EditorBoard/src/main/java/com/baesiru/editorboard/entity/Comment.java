package com.baesiru.editorboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment", indexes = @Index(name = "idx_comment_boardId", columnList = "board_id"))
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column
    private String content;
    @Column
    private String username;
    @Column
    private String password;
    @Column(name = "board_id")
    private Long boardId;
    @Column
    private Long parentId;
    @Column
    private Long depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
