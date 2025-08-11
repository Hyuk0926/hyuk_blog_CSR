package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.UserDto;
import com.example.hyuk_blog.service.UserService;
import com.example.hyuk_blog.service.JwtAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final JwtAuthService jwtAuthService;

    /**
     * 현재 로그인한 사용자 정보 조회
     * GET /api/user/info
     */
    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // 인증이 없는 경우 401 Unauthorized 반환
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7); // "Bearer " 제거
            String username = jwtAuthService.getUsernameFromToken(token);
            String role = jwtAuthService.getRoleFromToken(token);
            
            if (username == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 사용자 정보 조회
            Optional<UserDto> userInfoOpt = userService.findByUsername(username);
            
            if (userInfoOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }
            
            UserDto userInfo = userInfoOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("username", userInfo.getUsername());
            response.put("nickname", userInfo.getNickname());
            response.put("email", userInfo.getEmail());
            response.put("role", role);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user info: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user info");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * 사용자명 중복 확인 API
     * GET /api/user/check-username
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
     * GET /api/user/check-nickname
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
     * GET /api/user/check-email
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
