package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.PasswordResetConfirmDto;
import com.example.hyuk_blog.dto.PasswordResetRequestDto;
import com.example.hyuk_blog.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
public class PasswordResetApiController {

    private final PasswordResetService passwordResetService;

    /**
     * 비밀번호 재설정 요청
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDto requestDto) {
        
        log.info("비밀번호 재설정 요청: {}", requestDto.getEmail());
        
        try {
            passwordResetService.requestPasswordReset(requestDto);
            return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 이메일이 발송되었습니다."));
        } catch (Exception e) {
            log.error("비밀번호 재설정 요청 실패: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 이메일이 발송되었습니다."));
        }
    }

    /**
     * 비밀번호 재설정 토큰 검증
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        
        log.info("비밀번호 재설정 토큰 검증: {}", token);
        
        boolean isValid = passwordResetService.validateToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "유효한 토큰입니다."
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "유효하지 않거나 만료된 토큰입니다."
            ));
        }
    }

    /**
     * 비밀번호 재설정
     */
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDto confirmDto) {
        
        log.info("비밀번호 재설정 확인: {}", confirmDto.getToken());
        
        try {
            passwordResetService.resetPassword(confirmDto);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 재설정되었습니다."));
        } catch (Exception e) {
            log.error("비밀번호 재설정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
