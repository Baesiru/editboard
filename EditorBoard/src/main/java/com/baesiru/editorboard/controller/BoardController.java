package com.baesiru.editorboard.controller;

import com.baesiru.editorboard.dto.board.RequestBoard;
import com.baesiru.editorboard.dto.board.RequestBoardInfo;
import com.baesiru.editorboard.dto.board.ResponseBoard;
import com.baesiru.editorboard.dto.board.ResponseBoards;
import com.baesiru.editorboard.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/api/boards")
    public ResponseEntity<Object> getBoards(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<ResponseBoards> responseBoards = boardService.getBoards(page, size);
        return ResponseEntity.ok(responseBoards);
    }

    @PostMapping("/api/board")
    public ResponseEntity<Object> createBoard(@RequestBody RequestBoard requestBoard) {
        boardService.createBoard(requestBoard);
        return ResponseEntity.ok("게시글이 성공적으로 작성되었습니다.");
    }

    @GetMapping("/api/board/{id}")
    public ResponseEntity<Object> getBoard(@PathVariable Long id) {
        ResponseBoard responseBoard = boardService.getBoard(id);
        return ResponseEntity.ok(responseBoard);
    }

    @PostMapping("/api/board/{id}/delete")
    public ResponseEntity<Object> deleteBoard(@PathVariable Long id, @RequestBody RequestBoardInfo requestBoardInfo) {
        boardService.deleteBoard(id, requestBoardInfo);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @PostMapping("/api/board/{id}/check")
    public ResponseEntity<Object> checkPassword(@PathVariable Long id, @RequestBody RequestBoardInfo requestBoardInfo) {
        boardService.checkPassword(id, requestBoardInfo);
        return ResponseEntity.ok("비밀번호가 일치합니다.");
    }

    @PostMapping("/api/board/{id}/update")
    public ResponseEntity<Object> updateBoard(@PathVariable Long id, @RequestBody RequestBoard requestBoard) {
        boardService.updateBoard(id, requestBoard);
        return ResponseEntity.ok("수정이 완료되었습니다.");
    }
}
