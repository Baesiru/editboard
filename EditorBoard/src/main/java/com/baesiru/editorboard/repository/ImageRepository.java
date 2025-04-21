package com.baesiru.editorboard.repository;

import com.baesiru.editorboard.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByFilename(String filename);
    List<Image> findByBoardId(Long boardId);
    List<Image> findByBoardIdIsNullAndCreatedAtBefore(LocalDateTime createdAt);
    @Modifying
    @Query("DELETE FROM Image i WHERE i.boardId = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);
}
