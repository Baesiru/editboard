package com.baesiru.editorboard.service;

import com.baesiru.editorboard.dto.board.*;
import com.baesiru.editorboard.entity.Board;
import com.baesiru.editorboard.entity.Image;
import com.baesiru.editorboard.exception.board.BoardErrorCode;
import com.baesiru.editorboard.exception.board.BoardNotFoundException;
import com.baesiru.editorboard.exception.board.WrongBoardPasswordException;
import com.baesiru.editorboard.exception.image.ImageErrorCode;
import com.baesiru.editorboard.exception.image.ImageNotFoundException;
import com.baesiru.editorboard.repository.BoardRepository;
import com.baesiru.editorboard.repository.ImageRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Page<ResponseBoards> getBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseBoards> boards = boardRepository.getBoards(pageable);
        return boards;
    }

    @Transactional
    public void createBoard(RequestBoard requestBoard) {
        Board board = modelMapper.map(requestBoard, Board.class);
        board.setViewCount(0L);
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());
        board.setPassword(BCrypt.hashpw(requestBoard.getPassword(), BCrypt.gensalt()));
        board = boardRepository.save(board);
        Long boardId = board.getId();
        List<String> filenames = filenameExtractor(board.getContent());
        updateBoardIdForImages(boardId, filenames);
    }

    @Transactional
    @Retryable(
            value = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, maxDelay = 1000, multiplier = 2)
    )
    public ResponseBoard getBoard(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        Board newBoard = board.get();
        Long viewCount = newBoard.getViewCount();
        newBoard.setViewCount(viewCount + 1L);
        boardRepository.save(newBoard);
        ResponseBoard responseBoard = modelMapper.map(newBoard, ResponseBoard.class);
        return responseBoard;
    }

    @Transactional
    public ResponseBoard getBoardByPessimisticLock(Long id) {
        Optional<Board> board = boardRepository.findByIdWithPessimisticLock(id);
        if (board.isEmpty())
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        Board newBoard = board.get();
        newBoard.setViewCount(newBoard.getViewCount() + 1L);
        ResponseBoard responseBoard = modelMapper.map(newBoard, ResponseBoard.class);
        return responseBoard;
    }

    @Transactional
    public void deleteBoard(Long id, RequestBoardInfo requestBoardInfo) {
        checkPassword(id, requestBoardInfo);
        boardRepository.deleteById(id);
        List<Image> images = imageRepository.findByBoardId(id);
        if (!images.isEmpty()) {
            for (Image image : images) {
                image.setBoardId(null);
                imageRepository.save(image);
            }
        }
    }

    public void checkPassword(Long id, RequestBoardInfo requestBoardInfo) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        if (!BCrypt.checkpw(requestBoardInfo.getPassword(), board.get().getPassword()))
            throw new WrongBoardPasswordException(BoardErrorCode.WRONG_BOARD_PASSWORD);
    }

    @Transactional
    public void updateBoard(Long id, RequestBoardUpdate requestBoardUpdate) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            throw new BoardNotFoundException(BoardErrorCode.BOARD_NOT_FOUND);
        Board newBoard = board.get();
        if (!BCrypt.checkpw(requestBoardUpdate.getPassword(), newBoard.getPassword())) {
            throw new WrongBoardPasswordException(BoardErrorCode.WRONG_BOARD_PASSWORD);
        }
        newBoard.setTitle(requestBoardUpdate.getTitle());
        newBoard.setContent(requestBoardUpdate.getContent());
        newBoard.setUpdatedAt(LocalDateTime.now());
        boardRepository.save(newBoard);

        List<String> newImages = filenameExtractor(newBoard.getContent());
        List<Image> existsImage = imageRepository.findByBoardId(newBoard.getId());
        for (Image image : existsImage) {
            if (!newImages.contains(image.getFilename())) {
                image.setBoardId(null);
                imageRepository.save(image);
            }
            else {
                newImages.remove(image.getFilename());
            }
        }

        updateBoardIdForImages(id, newImages);
    }

    public List<String> filenameExtractor(String content) {

        String regex = "uploads/([a-zA-Z0-9_-]+(?:\\.[a-zA-Z0-9]+)?)";
        ArrayList<String> filenames = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String filename = matcher.group(1);
            filenames.add(filename);
        }

        return filenames;
    }

    public void updateBoardIdForImages(Long boardId, List<String> filenames) {
        for (String filename : filenames) {
            Optional<Image> image = imageRepository.findByFilename(filename);
            if (image.isEmpty()) {
                throw new ImageNotFoundException(ImageErrorCode.IMAGE_NOT_FOUND);
            }
            image.get().setBoardId(boardId);
            imageRepository.save(image.get());
        }
    }
}
