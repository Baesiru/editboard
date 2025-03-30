package com.baesiru.editorboard.repository;

import com.baesiru.editorboard.dto.board.ResponseBoards;
import com.baesiru.editorboard.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "select new com.baesiru.editorboard.dto.board.ResponseBoards(b.id, b.title, b.username, b.viewCount, b.createdAt, b.updatedAt) " +
            "from Board b " +
            "order by b.createdAt desc",
            countQuery = "select count(b) from Board b")
    Page<ResponseBoards> getBoards(Pageable pageable);
}
