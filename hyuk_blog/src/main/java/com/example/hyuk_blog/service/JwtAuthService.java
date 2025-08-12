package com.example.hyuk_blog.service;

import com.example.hyuk_blog.dto.JwtLoginRequestDto;
import com.example.hyuk_blog.dto.JwtResponseDto;
import com.example.hyuk_blog.entity.Admin;
import com.example.hyuk_blog.entity.User;
import com.example.hyuk_blog.repository.AdminRepository;
import com.example.hyuk_blog.repository.UserRepository;
import com.example.hyuk_blog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JwtAuthService {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 관리자 JWT 로그인
     */
    public JwtResponseDto adminLogin(JwtLoginRequestDto loginRequest) {
        log.info("관리자 로그인 시도: {}", loginRequest.getUsername());
        
        Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    log.error("관리자 계정을 찾을 수 없음: {}", loginRequest.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        log.info("관리자 계정 찾음: {}", admin.getUsername());
        log.debug("비밀번호 검증 진행 중...");

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            log.error("비밀번호 불일치: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // 비밀번호 업그레이드 확인 및 처리
        if (passwordEncoder.upgradeEncoding(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            adminRepository.save(admin);
            log.info("관리자 '{}'의 비밀번호를 새로운 방식으로 업그레이드했습니다.", admin.getUsername());
        }

        log.info("관리자 로그인 성공: {}", admin.getUsername());
        String token = jwtUtil.generateToken(admin.getUsername(), "ROLE_ADMIN");
        return new JwtResponseDto(token, admin.getUsername(), "ADMIN", "관리자 로그인 성공");
    }

    /**
     * 사용자 JWT 로그인
     */
    public JwtResponseDto userLogin(JwtLoginRequestDto loginRequest) {
        log.info("사용자 로그인 시도: {}", loginRequest.getUsername());
        
        // 1. User 테이블에서 사용자 찾기
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        
        // 2. User 테이블에 없으면 Admin 테이블에서 확인
        if (user == null) {
            Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        log.error("사용자 계정을 찾을 수 없음: {}", loginRequest.getUsername());
                        return new BadCredentialsException("Invalid username or password");
                    });
            
            // Admin 계정 비밀번호 검증
            boolean adminPasswordValid = false;
            
            // BCrypt 검증
            if (passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                adminPasswordValid = true;
            }
            // 평문 비교 (기존 admin 계정 호환성)
            else if (admin.getPassword().equals(loginRequest.getPassword())) {
                adminPasswordValid = true;
            }
            
            if (!adminPasswordValid) {
                log.error("Admin 비밀번호 불일치: {}", loginRequest.getUsername());
                throw new BadCredentialsException("Invalid username or password");
            }
            
            // 비밀번호 업그레이드 확인 및 처리
            if (passwordEncoder.upgradeEncoding(admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
                adminRepository.save(admin);
                log.info("관리자 '{}'의 비밀번호를 새로운 방식으로 업그레이드했습니다.", admin.getUsername());
            }
            
            log.info("Admin 로그인 성공: {}", admin.getUsername());
            String token = jwtUtil.generateToken(admin.getUsername(), "ROLE_ADMIN");
            return new JwtResponseDto(token, admin.getUsername(), "ADMIN", "관리자 로그인 성공");
        }

        log.info("사용자 계정 찾음: {}", user.getUsername());
        log.debug("비밀번호 검증 진행 중...");

        // DelegatingPasswordEncoder를 사용한 통합 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.error("비밀번호 불일치: {}", loginRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        // 비밀번호 업그레이드 확인 및 처리
        if (passwordEncoder.upgradeEncoding(user.getPassword())) {
            user.updatePassword(passwordEncoder.encode(loginRequest.getPassword()));
            userRepository.save(user);
            log.info("사용자 '{}'의 비밀번호를 새로운 방식으로 업그레이드했습니다.", user.getUsername());
        }

        log.info("사용자 로그인 성공: {}", user.getUsername());
        String token = jwtUtil.generateToken(user.getUsername(), "ROLE_USER");
        return new JwtResponseDto(token, user.getUsername(), "USER", "사용자 로그인 성공");
    }

    /**
     * 통합 로그인 (사용자/관리자)
     */
    public JwtResponseDto login(JwtLoginRequestDto loginRequest) {
        log.info("통합 로그인 시도: {}", loginRequest.getUsername());
        
        // 먼저 관리자 계정에서 확인
        try {
            return adminLogin(loginRequest);
        } catch (BadCredentialsException e) {
            log.debug("관리자 로그인 실패, 사용자 계정 확인: {}", loginRequest.getUsername());
        }
        
        // 관리자 계정이 아니면 사용자 계정에서 확인
        return userLogin(loginRequest);
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
            log.debug("Extracting username from token: {}", cleanToken.substring(0, Math.min(20, cleanToken.length())) + "...");
            String username = jwtUtil.extractUsername(cleanToken);
            log.debug("Extracted username: {}", username);
            return username;
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
            log.debug("Extracting role from token: {}", cleanToken.substring(0, Math.min(20, cleanToken.length())) + "...");
            String role = jwtUtil.extractRole(cleanToken);
            log.debug("Extracted role: {}", role);
            return role;
        } catch (Exception e) {
            log.error("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }
} 