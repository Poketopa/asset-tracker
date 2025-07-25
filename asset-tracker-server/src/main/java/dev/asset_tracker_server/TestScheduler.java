package dev.asset_tracker_server;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TestScheduler {
    @Scheduled(fixedRate = 1000)
    public void runEveryMinute() {
        System.out.println("1초마다 동작 중...");
    }
} 