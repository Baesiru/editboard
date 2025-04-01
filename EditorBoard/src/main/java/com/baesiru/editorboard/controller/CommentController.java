package com.baesiru.editorboard.controller;

import com.baesiru.editorboard.dto.comment.RequestComment;
import com.baesiru.editorboard.dto.comment.RequestCommentInfo;
import com.baesiru.editorboard.dto.comment.RequestCommentUpdate;
import com.baesiru.editorboard.dto.comment.ResponseComment;
import com.baesiru.editorboard.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/api/board/{boardId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long boardId, @RequestBody @Valid RequestComment requestComment) {
        commentService.createComment(boardId, requestComment);
        return ResponseEntity.ok("댓글이 성공적으로 작성되었습니다.");
    }

    @GetMapping("/api/board/{boardId}/comment")
    public ResponseEntity<Object> getComment(@PathVariable Long boardId) {
        List<ResponseComment> comments = commentService.getComment(boardId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/api/comment/{commentId}/delete")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId, @RequestBody @Valid RequestCommentInfo requestCommentInfo) {
        commentService.deleteComment(commentId, requestCommentInfo);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    @PostMapping("/api/comment/{commentId}/check")
    public ResponseEntity<Object> checkComment(@PathVariable Long commentId, @RequestBody @Valid RequestCommentInfo requestCommentInfo) {
        commentService.checkPassword(commentId, requestCommentInfo);
        return ResponseEntity.ok("비밀번호가 일치합니다.");
    }

    @PostMapping("/api/comment/{commentId}/update")
    public ResponseEntity<Object> updateComment(@PathVariable Long commentId, @RequestBody @Valid RequestCommentUpdate requestCommentUpdate) {
        commentService.updateComment(commentId, requestCommentUpdate);
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }
}
