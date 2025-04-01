package com.baesiru.editorboard.service;

import com.baesiru.editorboard.dto.board.*;
import com.baesiru.editorboard.entity.Board;
import com.baesiru.editorboard.entity.Image;
import com.baesiru.editorboard.repository.BoardRepository;
import com.baesiru.editorboard.repository.ImageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
        board = boardRepository.save(board);
        Long boardId = board.getId();
        List<String> filenames = filenameExtractor(board.getContent());
        updateBoardIdForImages(boardId, filenames);
    }

    @Transactional
    public ResponseBoard getBoard(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        Long viewCount = board.get().getViewCount();
        board.get().setViewCount(viewCount + 1L);
        boardRepository.save(board.get());
        ResponseBoard responseBoard = modelMapper.map(board.get(), ResponseBoard.class);
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
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        if (!board.get().getPassword().equals(requestBoardInfo.getPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    @Transactional
    public void updateBoard(Long id, RequestBoardUpdate requestBoardUpdate) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty())
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        Board newBoard = board.get();
        if (!newBoard.getPassword().equals(requestBoardUpdate.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
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
                throw new IllegalArgumentException("이미지가 존재하지 않습니다.");
            }
            image.get().setBoardId(boardId);
            imageRepository.save(image.get());
        }
    }
}
