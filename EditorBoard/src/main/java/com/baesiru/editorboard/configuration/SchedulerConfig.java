package com.baesiru.editorboard.configuration;

import com.baesiru.editorboard.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class SchedulerConfig {
    private final ImageService imageService;

    @Scheduled(cron = "0 0 * * * *") // 1시간 마다 수행
    @Transactional
    public void cleanOrphanImages() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime beforeHour = today.minusHours(3);
        imageService.deleteImage(beforeHour);

    }

}
