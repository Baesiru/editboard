package com.baesiru.editorboard.repository;

import com.baesiru.editorboard.dto.comment.ResponseComment;
import com.baesiru.editorboard.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);

    @Query(value = "select new com.baesiru.editorboard.dto.comment.ResponseComment(c.id, c.content, c.username, c.parentId, c.depth, c.createdAt, c.updatedAt) " +
    "from Comment c " +
    "where c.boardId = :boardId " +
    "order by c.parentId asc, c.depth asc, c.id asc")
    List<ResponseComment> findAllByBoardIdOrdered(@Param("boardId") Long boardId);
}
