package com.baesiru.editorboard.service;

import com.baesiru.editorboard.configuration.FileStorageProperties;
import com.baesiru.editorboard.entity.Image;
import com.baesiru.editorboard.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ImageService {
    private final Path uploadDir;
    private final ImageRepository imageRepository;
    public ImageService(FileStorageProperties fileStorageProperties, ImageRepository imageRepository) {
        this.uploadDir = fileStorageProperties.getUploadDir();
        this.imageRepository = imageRepository;

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("지정된 경로에 폴더를 생성할 수가 없습니다.", e);
        }
    }

    public String uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new NoSuchElementException("파일이 존재하지 않습니다.");
        }
        String fileName = image.getOriginalFilename();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String saveFileName = uuid + "_" + fileName;
        String fileFullPath = uploadDir.resolve(saveFileName).toAbsolutePath().toString();
        Image imageEntity = new Image();
        imageEntity.setFilename(saveFileName);
        imageRepository.save(imageEntity);

        try {
            File uploadFile = new File(fileFullPath);
            image.transferTo(uploadFile);
            return saveFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
