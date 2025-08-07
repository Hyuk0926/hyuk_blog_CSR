package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.JwtLoginRequestDto;
import com.example.hyuk_blog.dto.JwtResponseDto;
import com.example.hyuk_blog.service.JwtAuthService;
import com.example.hyuk_blog.repository.AdminRepository;
import com.example.hyuk_blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class JwtAuthController {

    private final JwtAuthService jwtAuthService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    // API 메서드들은 AuthApiController로 이동되었습니다.
    // 이 컨트롤러는 웹 페이지용으로만 사용됩니다.

    /**
     * 데이터베이스 계정 정보 확인 (테스트용)
     */
    @GetMapping("/debug/accounts")
    public ResponseEntity<?> getAccountInfo() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // 관리자 계정 정보
            List<Map<String, Object>> admins = new ArrayList<>();
            adminRepository.findAll().forEach(admin -> {
                Map<String, Object> adminInfo = new HashMap<>();
                adminInfo.put("username", admin.getUsername());
                adminInfo.put("password", admin.getPassword());
                adminInfo.put("active", admin.isActive());
                admins.add(adminInfo);
            });
            response.put("admins", admins);
            
            // 사용자 계정 정보
            List<Map<String, Object>> users = new ArrayList<>();
            userRepository.findAll().forEach(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", user.getUsername());
                userInfo.put("password", user.getPassword());
                userInfo.put("active", user.isActive());
                users.add(userInfo);
            });
            response.put("users", users);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Account info error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get account info");
            return ResponseEntity.internalServerError().body(error);
        }
    }
} 