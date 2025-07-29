package dev.asset_tracker_server.service;

import dev.asset_tracker_server.api.dto.UserLoginDto;
import dev.asset_tracker_server.api.dto.UserSignupDto;
import dev.asset_tracker_server.entity.User;
import dev.asset_tracker_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long signup(UserSignupDto dto) {
        if (userRepository.existsByLoginId(dto.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID입니다.");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        User user = User.builder()
                .loginId(dto.loginId())
                .password(dto.password()) // 👉 나중에 BCrypt 암호화 예정
                .nickname(dto.nickname())
                .email(dto.email())
                .build();

        userRepository.save(user);
        return user.getId();
    }

    public User login(UserLoginDto dto) {
        return userRepository.findByLoginId(dto.loginId())
                .filter(user -> user.getPassword().equals(dto.password()))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패: 아이디 또는 비밀번호 오류"));
    }
}
