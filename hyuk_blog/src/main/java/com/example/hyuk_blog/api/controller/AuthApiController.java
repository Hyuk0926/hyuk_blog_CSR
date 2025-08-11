package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.JwtLoginRequestDto;
import com.example.hyuk_blog.dto.JwtResponseDto;
import com.example.hyuk_blog.dto.UserRegistrationDto;
import com.example.hyuk_blog.service.JwtAuthService;
import com.example.hyuk_blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final JwtAuthService jwtAuthService;
    private final UserService userService;

    /**
     * 통합 로그인 API (사용자/관리자)
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtLoginRequestDto loginRequest) {
        try {
            JwtResponseDto response = jwtAuthService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 사용자 회원가입 API
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto registrationDto) {
        try {
            boolean success = userService.registerUser(registrationDto);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "회원가입이 성공적으로 완료되었습니다.");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "회원가입에 실패했습니다.");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * JWT 토큰 검증 API
     * POST /api/auth/validate
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
     * JWT 토큰 정보 조회 API
     * GET /api/auth/info
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
     * 사용자명 중복 확인 API
     * GET /api/auth/check-username
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Username check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "사용자명 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 닉네임 중복 확인 API
     * GET /api/auth/check-nickname
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        try {
            boolean available = userService.isNicknameAvailable(nickname);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("nickname", nickname);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Nickname check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "닉네임 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 이메일 중복 확인 API
     * GET /api/auth/check-email
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Email check error: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "이메일 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
