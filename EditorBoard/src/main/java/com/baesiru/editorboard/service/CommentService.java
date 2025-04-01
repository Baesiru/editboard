package com.baesiru.editorboard.service;

import com.baesiru.editorboard.dto.board.RequestBoardInfo;
import com.baesiru.editorboard.dto.comment.RequestComment;
import com.baesiru.editorboard.dto.comment.RequestCommentInfo;
import com.baesiru.editorboard.dto.comment.RequestCommentUpdate;
import com.baesiru.editorboard.dto.comment.ResponseComment;
import com.baesiru.editorboard.entity.Board;
import com.baesiru.editorboard.entity.Comment;
import com.baesiru.editorboard.repository.BoardRepository;
import com.baesiru.editorboard.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Transactional
    public void createComment(Long boardId, RequestComment requestComment) {
        Optional<Board> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        log.info(requestComment.toString());
        modelMapper.typeMap(RequestComment.class, Comment.class).addMappings(m -> m.skip(Comment::setId));
        Comment comment = modelMapper.map(requestComment, Comment.class);
        if (requestComment.getParentId() != null) {
            Optional<Comment> parentComment = commentRepository.findById(requestComment.getParentId());
            if (parentComment.isEmpty()) {
                throw new IllegalArgumentException("상위 댓글이 존재하지 않습니다.");
            }
            comment.setDepth(parentComment.get().getDepth() + 1L);
        }
        else {
            comment.setDepth(0L);
        }
        comment.setBoardId(boardId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public List<ResponseComment> getComment(Long boardId) {
        Optional<Board> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }

        List<ResponseComment> responseComments = commentRepository.findAllByBoardIdOrdered(boardId);
        return responseComments;
    }

    @Transactional
    public void deleteComment(Long commentId, RequestCommentInfo requestCommentInfo) {
        checkPassword(commentId, requestCommentInfo);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateComment(Long commentId, RequestCommentUpdate requestCommentUpdate) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        Comment newComment = comment.get();
        if (!newComment.getPassword().equals(requestCommentUpdate.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        newComment.setContent(requestCommentUpdate.getContent());
        newComment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(newComment);
    }

    public void checkPassword(Long id, RequestCommentInfo requestCommentInfo) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty())
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        if (!comment.get().getPassword().equals(requestCommentInfo.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
}
