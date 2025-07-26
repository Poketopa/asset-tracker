package dev.asset_tracker_server.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Tag(name = "Test", description = "테스트용 API")
@RestController
@RequestMapping("/api/test")
public class TestController {
    @Operation(summary = "서버 상태 확인", description = "Spring 서버가 정상적으로 작동하는지 확인하는 엔드포인트입니다.")
    @GetMapping
    public Map<String, String> sayHello() {
        return Map.of("message", "Spring 서버 잘 작동 중!");
    }
} 
