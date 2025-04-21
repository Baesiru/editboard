package com.baesiru.editorboard.service;

import com.baesiru.editorboard.configuration.FileStorageProperties;
import com.baesiru.editorboard.entity.Image;
import com.baesiru.editorboard.exception.image.FileNotExistException;
import com.baesiru.editorboard.exception.image.FolderCreationException;
import com.baesiru.editorboard.exception.image.ImageDeleteFailException;
import com.baesiru.editorboard.exception.image.ImageErrorCode;
import com.baesiru.editorboard.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    private final Path uploadDir;
    private final ImageRepository imageRepository;

    public ImageService(FileStorageProperties fileStorageProperties, ImageRepository imageRepository) {
        this.uploadDir = fileStorageProperties.getUploadDir();
        this.imageRepository = imageRepository;

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new FolderCreationException(ImageErrorCode.FOLDER_CREATE_ERROR);
        }
    }

    @Transactional
    public String uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new FileNotExistException(ImageErrorCode.FILE_NOT_EXIST);
        }
        String fileName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFileName = uuid + "_" + fileName;
        String fileFullPath = uploadDir.resolve(saveFileName).toAbsolutePath().toString();
        Image imageEntity = new Image();
        imageEntity.setFilename(saveFileName);
        imageEntity.setCreatedAt(LocalDateTime.now());
        imageRepository.save(imageEntity);

        try {
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            return saveFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteImage(LocalDateTime beforeHour) {
        List<Image> orphanImages = imageRepository.findByBoardIdIsNullAndCreatedAtBefore(beforeHour);

        for (Image image : orphanImages) {
            deleteFile(image.getFilename());
            imageRepository.delete(image);
            log.info("삭제 완료 : {}", image.getFilename());
        }
    }

    private void deleteFile(String filename) {
        String fileFullPath = uploadDir.resolve(filename).toAbsolutePath().toString();
        File file = new File(fileFullPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.info("파일 삭제 실패 : {}", filename);
                throw new ImageDeleteFailException(ImageErrorCode.IMAGE_DELETE_FAIL);
            }
        }
    }
}
