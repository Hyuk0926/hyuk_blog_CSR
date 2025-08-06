package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.login.SignupRequestDto;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리
     * @param signupRequestDto 회원가입 요청 정보
     * @return 생성된 사용자 Entity
     */
    @Transactional
    public User register(SignupRequestDto signupRequestDto) {
        // 중복 검사
        if (userRepository.existsByUsername(signupRequestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
        if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        if (signupRequestDto.getEmail() != null && !signupRequestDto.getEmail().isEmpty()
                && userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setUsername(signupRequestDto.getUsername());
        // 비밀번호는 반드시 BCrypt로 암호화하여 저장합니다.
        user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        user.setNickname(signupRequestDto.getNickname());
        user.setEmail(signupRequestDto.getEmail());
        user.setActive(true);
        // TODO: Role 설정 로직 추가 (예: adminToken 확인)

        return userRepository.save(user);
    }

    // 사용자명으로 사용자 조회 (Spring Security에서 사용)
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }

    // 사용자명 존재 여부 확인
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 닉네임 존재 여부 확인
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 이메일 존재 여부 확인
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 