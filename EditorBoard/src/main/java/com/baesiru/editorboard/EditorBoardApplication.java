package com.baesiru.editorboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EditorBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(EditorBoardApplication.class, args);
    }

}
