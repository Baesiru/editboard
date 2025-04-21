package com.baesiru.editorboard.service;

import com.baesiru.editorboard.dto.board.RequestBoardInfo;
import com.baesiru.editorboard.dto.comment.RequestComment;
import com.baesiru.editorboard.dto.comment.RequestCommentInfo;
import com.baesiru.editorboard.dto.comment.RequestCommentUpdate;
import com.baesiru.editorboard.dto.comment.ResponseComment;
import com.baesiru.editorboard.entity.Board;
import com.baesiru.editorboard.entity.Comment;
import com.baesiru.editorboard.exception.board.BoardErrorCode;
import com.baesiru.editorboard.exception.board.BoardNotFoundException;
import com.baesiru.editorboard.exception.comment.CommentErrorCode;
import com.baesiru.editorboard.exception.comment.CommentNotFoundException;
import com.baesiru.editorboard.exception.comment.ParentCommentNotFoundException;
import com.baesiru.editorboard.exception.comment.WrongCommentPasswordException;
import com.baesiru.editorboard.repository.BoardRepository;
import com.baesiru.editorboard.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
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
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        }

        log.info(requestComment.toString());
        modelMapper.typeMap(RequestComment.class, Comment.class).addMappings(m -> m.skip(Comment::setId));
        Comment comment = modelMapper.map(requestComment, Comment.class);
        if (requestComment.getParentId() != null) {
            Optional<Comment> parentComment = commentRepository.findById(requestComment.getParentId());
            if (parentComment.isEmpty()) {
                throw new ParentCommentNotFoundException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
            comment.setDepth(parentComment.get().getDepth() + 1L);
        }
        else {
            comment.setDepth(0L);
        }
        comment.setPassword(BCrypt.hashpw(comment.getPassword(), BCrypt.gensalt()));
        comment.setBoardId(boardId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public List<ResponseComment> getComment(Long boardId) {
        Optional<Board> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        }

        List<ResponseComment> responseComments = commentRepository.findAllByBoardIdOrdered(boardId);
        return responseComments;
    }

    @Transactional
    public void deleteComment(Long commentId, RequestCommentInfo requestCommentInfo) {
        checkPassword(commentId, requestCommentInfo.getPassword());
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateComment(Long commentId, RequestCommentUpdate requestCommentUpdate) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new CommentNotFoundException(CommentErrorCode.COMMENT_NOT_FOUND);
        }
        Comment newComment = comment.get();
        checkPassword(commentId, requestCommentUpdate.getPassword());
        newComment.setContent(requestCommentUpdate.getContent());
        newComment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(newComment);
    }

    public void checkPassword(Long id, String currPassword) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty())
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        if (!BCrypt.checkpw(currPassword, comment.get().getPassword()))
            throw new WrongCommentPasswordException(CommentErrorCode.WRONG_COMMENT_PASSWORD);    }
}
