package com.example.hyuk_blog.api.controller;

import com.example.hyuk_blog.dto.AdminDto;
import com.example.hyuk_blog.service.AdminService;
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
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;
    private final JwtAuthService jwtAuthService;

    /**
     * 관리자 정보 조회 API
     * GET /api/admin/auth/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("Admin profile request received - Auth header: {}", authHeader != null ? "present" : "null");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid authorization header: {}", authHeader);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Authorization header is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            String token = authHeader.substring(7); // "Bearer " 제거
            log.info("Token extracted, length: {}", token.length());
            
            String username = jwtAuthService.getUsernameFromToken(token);
            String role = jwtAuthService.getRoleFromToken(token);
            
            log.info("Admin profile request - Username: {}, Role: {}", username, role);
            
            if (username == null) {
                log.warn("Username extraction failed from token");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid token");
                return ResponseEntity.badRequest().body(error);
            }

            // 관리자 권한 확인
            if (!"ROLE_ADMIN".equals(role)) {
                log.warn("Access denied - Expected ROLE_ADMIN, got: {}", role);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin access required");
                return ResponseEntity.status(403).body(error);
            }

            // 관리자 정보 조회
            Optional<AdminDto> adminInfoOpt = adminService.findByUsername(username);
            
            if (adminInfoOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin not found");
                return ResponseEntity.notFound().build();
            }
            
            AdminDto adminInfo = adminInfoOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", adminInfo);
            response.put("message", "관리자 정보를 성공적으로 조회했습니다.");
            
            log.info("Admin profile request successful for user: {}", username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get admin profile: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get admin profile");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * JWT 토큰 디버그 API (개발용)
     * GET /api/admin/auth/debug-token
     */
    @GetMapping("/debug-token")
    public ResponseEntity<?> debugToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Authorization header is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            String token = authHeader.substring(7);
            String username = jwtAuthService.getUsernameFromToken(token);
            String role = jwtAuthService.getRoleFromToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token_length", token.length());
            response.put("username", username);
            response.put("role", role);
            response.put("token_prefix", token.substring(0, Math.min(20, token.length())) + "...");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token debug failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token debug failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
