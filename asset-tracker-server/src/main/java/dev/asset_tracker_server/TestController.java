package dev.asset_tracker_server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public Map<String, String> sayHello() {
        return Map.of("message", "Spring 서버 잘 작동 중!");
    }
} 