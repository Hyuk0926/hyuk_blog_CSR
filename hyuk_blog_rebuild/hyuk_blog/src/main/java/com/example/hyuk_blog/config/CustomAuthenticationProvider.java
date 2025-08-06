package com.example.hyuk_blog.config;

import com.example.hyuk_blog.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    // PasswordEncoder는 순환 참조를 피하기 위해 필요할 때만 생성

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("CustomAuthenticationProvider 인증 시도: 사용자명={}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 1. BCrypt로 검증 (최신 방식)
        try {
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder bcryptEncoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            if (bcryptEncoder.matches(password, userDetails.getPassword())) {
                log.debug("BCrypt 비밀번호 검증 성공: 사용자명={}", username);
                return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
            }
        } catch (Exception e) {
            log.debug("BCrypt 검증 실패: {}", e.getMessage());
        }

        // 2. SHA-256으로 검증 (기존 DB 호환성)
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            if (userDetails.getPassword().equals(hexString.toString())) {
                log.debug("SHA-256 비밀번호 검증 성공: 사용자명={}", username);
                return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
            }
        } catch (Exception e) {
            log.debug("SHA-256 검증 중 오류: {}", e.getMessage());
        }
        
        // 3. 평문으로 검증 (레거시 데이터)
        if (userDetails.getPassword().equals(password)) {
            log.debug("평문 비밀번호 검증 성공: 사용자명={}", username);
            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        }

        // 모든 검증 실패 시
        log.warn("모든 비밀번호 검증 방식 실패: 사용자명={}", username);
        throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 