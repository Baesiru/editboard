package com.baesiru.editorboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//    @Version
//    private Long version = 0L;
}
