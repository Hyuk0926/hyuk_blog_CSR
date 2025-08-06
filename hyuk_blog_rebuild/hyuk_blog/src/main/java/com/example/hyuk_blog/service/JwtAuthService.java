package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.login.JwtLoginRequestDto;
import com.example.hyuk_blog.dto.login.JwtResponseDto;
import com.example.hyuk_blog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; // Spring Security의 인증 관리자

    /**
     * 사용자/관리자 통합 로그인
     * @param loginRequest 로그인 요청 정보
     * @return JWT 토큰 응답
     */
    public JwtResponseDto login(JwtLoginRequestDto loginRequest) {
        log.info("JwtAuthService 로그인 시작: 사용자명={}", loginRequest.getUsername());

        try {
            // 1. Spring Security의 AuthenticationManager를 통해 인증 시도
            log.debug("AuthenticationManager를 통한 인증 시도");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // 2. 인증 성공 시, 사용자 정보와 권한 추출
            String username = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String role = authorities.stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER"); // 기본값

            log.info("인증 성공: 사용자={}, 권한={}", username, role);

            // 3. JWT 토큰 생성
            log.debug("JWT 토큰 생성 시작");
            String token = jwtUtil.generateToken(username, role);
            log.debug("JWT 토큰 생성 완료");

            JwtResponseDto response = new JwtResponseDto(token, username, role, "로그인 성공");
            log.info("JwtAuthService 로그인 완료: 사용자명={}, 역할={}", username, role);
            
            return response;
        } catch (Exception e) {
            log.error("JwtAuthService 로그인 실패: 사용자명={}, 오류={}", loginRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * JWT 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            String cleanToken = jwtUtil.removeBearerPrefix(token);
            String username = jwtUtil.extractUsername(cleanToken);
            return jwtUtil.validateToken(cleanToken, username);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     */
    public String getUsernameFromToken(String token) {
        try {
            String cleanToken = jwtUtil.removeBearerPrefix(token);
            return jwtUtil.extractUsername(cleanToken);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JWT 토큰에서 역할 추출
     */
    public String getRoleFromToken(String token) {
        try {
            String cleanToken = jwtUtil.removeBearerPrefix(token);
            return jwtUtil.extractRole(cleanToken);
        } catch (Exception e) {
            log.error("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }
} 