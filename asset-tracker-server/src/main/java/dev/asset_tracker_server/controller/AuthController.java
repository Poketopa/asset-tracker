package dev.asset_tracker_server.controller;

import dev.asset_tracker_server.api.dto.UserLoginDto;
import dev.asset_tracker_server.api.dto.UserSignupDto;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupDto dto) {
        Long userId = userService.signup(dto);
        return ResponseEntity.ok("회원가입 완료! userId: " + userId);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
        User user = userService.login(dto);
        return ResponseEntity.ok("로그인 성공! userId: " + user.getId());
    }
}
