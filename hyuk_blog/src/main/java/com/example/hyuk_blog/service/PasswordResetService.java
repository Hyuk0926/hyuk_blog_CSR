package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.PasswordResetConfirmDto;
import com.example.hyuk_blog.dto.PasswordResetRequestDto;
import com.example.hyuk_blog.entity.PasswordResetToken;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.PasswordResetTokenRepository;
import com.example.hyuk_blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 비밀번호 재설정 요청을 처리합니다.
     */
    @Transactional
    public void requestPasswordReset(PasswordResetRequestDto requestDto) {
        String email = requestDto.getEmail();
        log.info("PasswordResetService.requestPasswordReset 시작: {}", email);
        
        try {
            // 사용자 존재 여부 확인
            Optional<User> userOpt = userRepository.findByEmail(email);
            log.info("사용자 조회 결과: {}", userOpt.isPresent());
            
            if (userOpt.isEmpty()) {
                log.warn("비밀번호 재설정 요청 - 존재하지 않는 이메일: {}", email);
                // 보안을 위해 존재하지 않는 이메일이어도 성공 응답
                return;
            }

            // 기존 토큰 삭제
            log.info("기존 토큰 삭제 시작");
            tokenRepository.deleteByEmail(email);
            log.info("기존 토큰 삭제 완료");

            // 새 토큰 생성
            log.info("새 토큰 생성 시작");
            String token = generateToken();
            log.info("토큰 생성 완료: {}", token);
            
            PasswordResetToken resetToken = new PasswordResetToken(token, email);
            log.info("PasswordResetToken 객체 생성 완료");
            
            tokenRepository.save(resetToken);
            log.info("토큰 저장 완료");

            // 이메일 발송
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            log.info("이메일 발송 시작: {}", resetUrl);
            emailService.sendPasswordResetEmail(email, token, resetUrl);
            log.info("이메일 발송 완료");

            log.info("비밀번호 재설정 이메일 발송 완료: {}", email);
        } catch (Exception e) {
            log.error("PasswordResetService.requestPasswordReset 오류: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 비밀번호 재설정 토큰을 검증합니다.
     */
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        return resetToken.isValid();
    }

    /**
     * 비밀번호를 재설정합니다.
     */
    @Transactional
    public void resetPassword(PasswordResetConfirmDto confirmDto) {
        // 토큰 검증
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(confirmDto.getToken());
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (!resetToken.isValid()) {
            throw new RuntimeException("만료되었거나 이미 사용된 토큰입니다.");
        }

        // 비밀번호 확인
        if (!confirmDto.getNewPassword().equals(confirmDto.getConfirmPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 찾기
        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 업데이트
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(confirmDto.getNewPassword()));
        userRepository.save(user);

        // 토큰 사용 처리
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("비밀번호 재설정 완료: {}", resetToken.getEmail());
    }

    /**
     * 안전한 토큰을 생성합니다.
     */
    private String generateToken() {
        StringBuilder token = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 32; i++) {
            token.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return token.toString();
    }
}
