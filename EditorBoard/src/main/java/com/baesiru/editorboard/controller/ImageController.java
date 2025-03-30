package com.baesiru.editorboard.controller;

import com.baesiru.editorboard.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;

    @PostMapping("/api/image")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile image) {
        String url = imageService.uploadImage(image);
        return ResponseEntity.ok().body(Collections.singletonMap("filename", url));
    }
}
