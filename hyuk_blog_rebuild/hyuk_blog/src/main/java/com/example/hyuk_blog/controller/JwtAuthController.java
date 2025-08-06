package com.example.hyuk_blog.controller;

import com.example.hyuk_blog.dto.login.JwtLoginRequestDto;
import com.example.hyuk_blog.dto.login.JwtResponseDto;
import com.example.hyuk_blog.dto.login.SignupRequestDto;
import com.example.hyuk_blog.service.JwtAuthService;
import com.example.hyuk_blog.service.UserService;
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
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class JwtAuthController {

    private final JwtAuthService jwtAuthService;
    private final UserService userService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    /**
     * 통합 JWT 로그인 (사용자/관리자)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtLoginRequestDto loginRequest) {
        log.info("=== 로그인 요청 수신 ===");
        log.info("요청 본문: {}", loginRequest);
        
        try {
            log.info("로그인 시도: 사용자명={}", loginRequest.getUsername());
            
            JwtResponseDto response = jwtAuthService.login(loginRequest);
            log.info("로그인 성공: 사용자명={}, 역할={}", response.getUsername(), response.getRole());
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("로그인 실패 (잘못된 자격증명): 사용자명={}, 오류={}", loginRequest.getUsername(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("로그인 오류: 사용자명={}, 오류={}", loginRequest.getUsername(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequestDto signupRequest) {
        log.info("=== 회원가입 요청 수신 ===");
        log.info("요청 본문: {}", signupRequest);
        
        try {
            log.info("회원가입 시도: 사용자명={}", signupRequest.getUsername());
            
            userService.register(signupRequest);
            log.info("회원가입 성공: 사용자명={}", signupRequest.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "회원가입이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패 (유효성 검증 오류): 사용자명={}, 오류={}", signupRequest.getUsername(), e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("회원가입 오류: 사용자명={}, 오류={}", signupRequest.getUsername(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * JWT 토큰 검증
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            boolean isValid = jwtAuthService.validateToken(token);
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            
            if (isValid) {
                response.put("username", jwtAuthService.getUsernameFromToken(token));
                response.put("role", jwtAuthService.getRoleFromToken(token));
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token validation failed");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * JWT 토큰 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<?> getTokenInfo(@RequestHeader("Authorization") String token) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("username", jwtAuthService.getUsernameFromToken(token));
            response.put("role", jwtAuthService.getRoleFromToken(token));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token info error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get token info");
            return ResponseEntity.badRequest().body(error);
        }
    }

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